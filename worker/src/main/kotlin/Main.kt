@file:JvmName("MainKt")

package me.i18u

import arrow.core.Option
import arrow.core.Some
import arrow.core.mapNotNull
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*
import kotlinx.datetime.*

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> =
    coroutineScope {
        map { async { f(it) } }.awaitAll()
    }

data class TramStopDeparture(
    val scheduleDepartureUtc: Instant,
    val estimatedDepartureUtc: Instant,
    val atPlatform: Boolean,
    val directionDescription: String,
    val directionName: String,
    val tramId: Option<String>,
    /**
     * A low floor and air-conditioned tram is a modern tram
     */
    val lowFloor: Option<Boolean>,
    /**
     * A low floor and air-conditioned tram is a modern tram
     */
    val airConditioned: Option<Boolean>,
)

data class TramStop(
    val stopId: Int,
    val stopName: String,
    val routeId: Int,
    val latitude: Double,
    val longitude: Double,
    val stopNumber: Int,
    val scheduledDepartures: List<TramStopDeparture>
)

/**
 * Convert this Instant to a String AttributeValue.
 */
fun Instant.asAttributeValue(): AttributeValue {
    return this.toString().asAttributeValue()
}

/**
 * Convert this Boolean to a Bool AttributeValue.
 */
fun Boolean.asAttributeValue(): AttributeValue {
    return AttributeValue.Bool(this)
}

/**
 * Convert this String to a String AttributeValue
 */
fun String.asAttributeValue(): AttributeValue {
    return AttributeValue.S(this)
}

/**
 * Convert this Int to a Number AttributeValue.
 */
fun Int.asAttributeValue(): AttributeValue {
    return AttributeValue.N(this.toString())
}

/**
 * Convert this Double to a Number AttributeValue.
 */
fun Double.asAttributeValue(): AttributeValue {
    return AttributeValue.N(this.toString())
}

/**
 * Convert this List to a List AttributeValue.
 */
fun List<AttributeValue>.asAttributeValue(): AttributeValue {
    return AttributeValue.L(this)
}

/**
 * Convert this Map to a Map AttributeValue.
 */
fun Map<String, AttributeValue>.asAttributeValue(): AttributeValue {
    return AttributeValue.M(this)
}

fun TramStopDeparture.toDynamoDbItem(): Map<String, AttributeValue> {
    return mapOf(
        "scheduleDepartureUtc" to this.scheduleDepartureUtc.asAttributeValue(),
        "estimatedDepartureUtc" to this.estimatedDepartureUtc.asAttributeValue(),
        "atPlatform" to this.atPlatform.asAttributeValue(),
        "directionDescription" to this.directionDescription.asAttributeValue(),
        "directionName" to this.directionName.asAttributeValue(),
        "tramId" to this.tramId.map { it.asAttributeValue() }.getOrNull(),
        "lowFloor" to this.lowFloor.map { it.asAttributeValue() }.getOrNull(),
        "airConditioned" to this.airConditioned.map { it.asAttributeValue() }.getOrNull(),
    ).mapNotNull { it.value }
}

fun TramStop.toDynamoDbItem(): Map<String, AttributeValue> {
    return mapOf(
        "stopId" to this.stopId.asAttributeValue(),
        "stopName" to this.stopName.asAttributeValue(),
        "routeId" to this.routeId.asAttributeValue(),
        "latitude" to this.latitude.asAttributeValue(),
        "longitude" to this.longitude.asAttributeValue(),
        "stopNumber" to this.stopNumber.asAttributeValue(),
        "scheduledDepartures" to this.scheduledDepartures.map {
            it.toDynamoDbItem().asAttributeValue()
        }.asAttributeValue()

    )
}

suspend fun main() {
    val concurrencyLimit = 10
    val client = HttpClient(CIO)
    val ptvClient = PtvApiClient(
        client,
        System.getenv("PTV_DEV_ID"),
        System.getenv("PTV_API_KEY")
    )
    val dynamoTableName = System.getenv("DYNAMO_TABLE")
    val routeTypeResponse = ptvClient.getRouteTypes()
    val routeTypes = routeTypeResponse.routeTypes
    val trams = routeTypes.single {
        it.routeTypeName == "Tram"
    }

    val tramRoutes = ptvClient.getRoutes(Some(listOf(trams))).routes
    val myTramRoute = tramRoutes.single {
        it.routeNumber == "109"
    }

    val directions = ptvClient.getDirections(myTramRoute.routeId).directions

    val desiredDirection = directions.single {
        it.directionName.lowercase() == "port melbourne"
    }

    val stops = ptvClient.getStops(
        myTramRoute.routeId,
        myTramRoute.routeType,
        stopDisruptions = true,
        includeGeopath = true,
        directionId = Some(desiredDirection.directionId)
    ).stops.sortedBy { it.stopSequence }

    val departuresForStop =
        stops
            .chunked(concurrencyLimit)
            .flatMap { stopChunk ->
                stopChunk.pmap { stop ->
                    print(".")
                    ptvClient.getDepartures(
                        stop.routeType,
                        stop.stopId,
                        myTramRoute.routeId,
                        directionId = Some(desiredDirection.directionId),
                        dateUtc = Some(Clock.System.now()),
                        maxResults = Some(10),
                        expand = Some(
                            listOf(
                                DepartureExpandOptions.RUN,
                                DepartureExpandOptions.VEHICLEDESCRIPTOR,
                                DepartureExpandOptions.VEHICLEPOSITION
                            )
                        )
                    )
                }
            }

    val departuresByStop = departuresForStop
        .flatMap { it.departures }
        .groupBy { it.stopId }

    val runByRunId = departuresForStop
        .flatMap { it.runs.values }
        .associateBy { it.runId }

    val tramStopInfos = stops.map { stop ->
        TramStop(
            stop.stopId,
            stop.stopName,
            myTramRoute.routeId,
            stop.stopLatitude,
            stop.stopLongitude,
            stop.stopSequence,
            departuresByStop[stop.stopId].orEmpty().map { departure ->
                val vehicleInfo = runByRunId[departure.runId]?.vehicleDescriptor

                TramStopDeparture(
                    departure.scheduledDepartureUtc!!,
                    departure.estimatedDepartureUtc ?: departure.scheduledDepartureUtc,
                    departure.atPlatform!!,
                    desiredDirection.routeDirectionDescription,
                    desiredDirection.directionName,
                    Option.fromNullable(vehicleInfo?.id),
                    Option.fromNullable(vehicleInfo?.lowFloor),
                    Option.fromNullable(vehicleInfo?.airConditioned)
                )
            }
        )
    }

    val dynamoClient = DynamoDbClient.builder()
        .apply {
            this.config.region = "ap-southeast-2"
        }
        .build()

    val response = tramStopInfos.chunked(25).map {
        dynamoClient.batchWriteItem(BatchWriteItemRequest {
            requestItems = it.groupBy(
                { dynamoTableName },
                { tramStopInfo ->
                    WriteRequest {
                        putRequest = PutRequest {
                            item = tramStopInfo.toDynamoDbItem()
                        }
                    }
                }
            )
        })
    }

    println(response)
}
