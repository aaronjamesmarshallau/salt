package me.i18u

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RouteType(
    @SerialName("route_type_name")
    val routeTypeName: String,
    @SerialName("route_type")
    val routeTypeId: Int
)

@Serializable
data class PtvApiStatus(
    @SerialName("version")
    val version: String,
    @SerialName("health")
    val health: Int
)

@Serializable
data class RouteTypeResponse(
    @SerialName("route_types")
    val routeTypes: List<RouteType>,
    @SerialName("status")
    val status: PtvApiStatus,
)

@Serializable
data class RouteServiceStatus(
    @SerialName("description")
    val description: String,
    @SerialName("timestamp")
    val timestamp: String,
)

@Serializable
data class Route(
    @SerialName("route_service_status")
    val routeServiceStatus: RouteServiceStatus,
    @SerialName("route_type")
    val routeType: Int,
    @SerialName("route_id")
    val routeId: Int,
    @SerialName("route_name")
    val routeName: String,
    @SerialName("route_number")
    val routeNumber: String,
    @SerialName("route_gtfs_id")
    val routeGtfsId: String,
    @SerialName("geopath")
    val geopath: List<String>,
)

@Serializable
data class RouteResponse(
    @SerialName("routes")
    val routes: List<Route>,
    @SerialName("status")
    val status: PtvApiStatus
)

@Serializable
data class StopTicket(
    @SerialName("ticket_type")
    val ticketType: String,
    @SerialName("zone")
    val zone: String,
    @SerialName("is_free_fare_zone")
    val isFreeFareZone: Boolean,
    @SerialName("ticket_machine")
    val ticketMachine: Boolean,
    @SerialName("ticket_checks")
    val ticketChecks: Boolean,
    @SerialName("vline_reservation")
    val vlineReservation: Boolean,
    @SerialName("ticket_zones")
    val ticketZones: List<Int>,
)

@Serializable
data class Stop(
    @SerialName("disruption_ids")
    val disruptionIds: List<Int>,
    @SerialName("stop_suburb")
    val stopSuburb: String,
    @SerialName("route_type")
    val routeType: Int,
    @SerialName("stop_latitude")
    val stopLatitude: Double,
    @SerialName("stop_longitude")
    val stopLongitude: Double,
    @SerialName("stop_sequence")
    val stopSequence: Int,
    @SerialName("stop_ticket")
    val stopTicket: StopTicket,
    @SerialName("stop_id")
    val stopId: Int,
    @SerialName("stop_name")
    val stopName: String,
    @SerialName("stop_landmark")
    val stopLandmark: String,
)

@Serializable
data class StopGeopath(
    @SerialName("direction_id")
    val directionId: Int,
    @SerialName("valid_from")
    val validFrom: String,
    @SerialName("valid_to")
    val validTo: String,
    @SerialName("paths")
    val paths: List<String>,
)

@Serializable
data class StopDisruptionRouteDirection(
    @SerialName("route_direction_id")
    val routeDirectionId: Int,
    @SerialName("direction_id")
    val directionId: Int,
    @SerialName("direction_name")
    val directionName: String,
    @SerialName("service_time")
    val serviceTime: String,
)

@Serializable
data class StopDisruptionRoute(
    @SerialName("route_type")
    val routeType: Int,
    @SerialName("route_id")
    val routeId: Int,
    @SerialName("route_name")
    val routeName: String,
    @SerialName("route_number")
    val routeNumber: String,
    @SerialName("route_gtfs_id")
    val routeGtfsId: String,
    @SerialName("direction")
    val direction: StopDisruptionRouteDirection?,
)

@Serializable
data class StopDisruptionStop(
    @SerialName("stop_id")
    val stopId: Int,
    @SerialName("stop_name")
    val stopName: String,
)

@Serializable
data class StopDisruption(
    @SerialName("disruption_id")
    val disruptionId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String,
    @SerialName("description")
    val description: String,
    @SerialName("disruption_status")
    val disruptionStatus: String,
    @SerialName("disruption_type")
    val disruptionType: String,
    @SerialName("published_on")
    val publishedOn: String,
    @SerialName("last_updated")
    val lastUpdated: String,
    @SerialName("from_date")
    val fromDate: String,
    @SerialName("to_date")
    val toDate: String?,
    @SerialName("routes")
    val routes: List<StopDisruptionRoute>,
    @SerialName("stops")
    val stops: List<StopDisruptionStop>,
    @SerialName("colour")
    val colour: String? = null,
    @SerialName("display_on_board")
    val displayOnBoard: Boolean?,
    @SerialName("display_status")
    val displayStatus: Boolean?,
)

@Serializable
data class StopsResponse(
    @SerialName("stops")
    val stops: List<Stop>,
    @SerialName("disruptions")
    val disruptions: Map<String, StopDisruption>,
    @SerialName("geopath")
    val geopath: List<StopGeopath>,
    @SerialName("status")
    val status: PtvApiStatus
)

@Serializable
data class Direction(
    @SerialName("route_direction_description")
    val routeDirectionDescription: String,
    @SerialName("direction_id")
    val directionId: Int,
    @SerialName("direction_name")
    val directionName: String,
    @SerialName("route_id")
    val routeId: Int,
    @SerialName("route_type")
    val routeType: Int,
)

@Serializable
data class DirectionsResponse(
    @SerialName("directions")
    val directions: List<Direction>,
    @SerialName("status")
    val status: PtvApiStatus,
)

@Serializable
data class Departure(
    @SerialName("stop_id")
    val stopId: Int?,
    @SerialName("route_id")
    val routeId: Int?,
    @SerialName("run_id")
    val runId: Int?,
    @SerialName("run_ref")
    val runRef: String?,
    @SerialName("direction_id")
    val directionId: Int?,
    @SerialName("disruption_ids")
    val disruptionIds: List<Int>?,
    @SerialName("scheduled_departure_utc")
    val scheduledDepartureUtc: Instant?,
    @SerialName("estimated_departure_utc")
    val estimatedDepartureUtc: Instant?,
    @SerialName("at_platform")
    val atPlatform: Boolean?,
    @SerialName("platform_number")
    val platformNumber: String?,
    @SerialName("flags")
    val flags: String?,
    @SerialName("departure_sequence")
    val departureSequence: Int?,
)

@Serializable
data class DeparturesResponse(
    @SerialName("departures")
    val departures: List<Departure>,
    @SerialName("stops")
    val stops: Map<String, String>,
    @SerialName("routes")
    val routes: Map<String, String>,
    @SerialName("runs")
    val runs: Map<String, Run>,
    @SerialName("directions")
    val directions: Map<String, String>,
    @SerialName("disruptions")
    val disruptions: Map<String, String>,
    @SerialName("status")
    val status: PtvApiStatus,
)

@Serializable
data class VehiclePosition(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("easting")
    val easting: Double,
    @SerialName("northing")
    val northing: Double,
    @SerialName("direction")
    val direction: String,
    @SerialName("bearing")
    val bearing: Double,
    @SerialName("supplier")
    val supplier: String,
    @SerialName("datetime_utc")
    val datetimeUtc: Instant?,
    @SerialName("expiry_time")
    val expiryTime: Instant?,
)

@Serializable
data class VehicleDescriptor(
    @SerialName("operator")
    val operator: String,
    @SerialName("id")
    val id: String,
    @SerialName("low_floor")
    val lowFloor: Boolean,
    @SerialName("air_conditioned")
    val airConditioned: Boolean,
    @SerialName("description")
    val description: String?,
    @SerialName("supplier")
    val supplier: String,
    @SerialName("length")
    val length: String?,
)

@Serializable
data class Run(
    @SerialName("run_id")
    val runId: Int,
    @SerialName("run_ref")
    val runRef: String,
    @SerialName("route_id")
    val routeId: Int,
    @SerialName("route_type")
    val routeType: Int,
    @SerialName("final_stop_id")
    val finalStopId: Int,
    @SerialName("destination_name")
    val destinationName: String,
    @SerialName("status")
    val status: String,
    @SerialName("direction_id")
    val directionId: Int,
    @SerialName("run_sequence")
    val runSequence: Int,
    @SerialName("express_stop_count")
    val expressStopCount: Int,
    @SerialName("vehicle_position")
    val vehiclePosition: VehiclePosition?,
    @SerialName("vehicle_descriptor")
    val vehicleDescriptor: VehicleDescriptor?,
    @SerialName("geopath")
    val geopath: List<StopGeopath>
)

@Serializable
data class RunsResponse(
    @SerialName("runs")
    val runs: List<Run>,
    @SerialName("status")
    val status: PtvApiStatus,
)

@Serializable
data class SkippedStop(
    @SerialName("stop_distance")
    var stopDistance: Double? = null,
    @SerialName("stop_suburb")
    var stopSuburb: String? = null,
    @SerialName("stop_name")
    var stopName: String? = null,
    @SerialName("stop_id")
    var stopId: Int? = null,
    @SerialName("route_type")
    var routeType: Int? = null,
    @SerialName("stop_latitude")
    var stopLatitude: Double? = null,
    @SerialName("stop_longitude")
    var stopLongitude: Double? = null,
    @SerialName("stop_landmark")
    var stopLandmark: String? = null,
    @SerialName("stop_sequence")
    var stopSequence: Int? = null
)

@Serializable
data class PatternDeparture(
    @SerialName("skipped_stops")
    val skippedStops: List<SkippedStop>,
    @SerialName("stop_id")
    val stopId: Int,
    @SerialName("route_id")
    val routeId: Int,
    @SerialName("run_id")
    val runId: Int,
    @SerialName("run_ref")
    val runRef: String,
    @SerialName("direction_id")
    val directionId: Int,
    @SerialName("disruption_ids")
    val disruptionIds: List<Int>,
    @SerialName("scheduled_departure_utc")
    val scheduledDepartureUtc: Instant?,
    @SerialName("estimated_departure_utc")
    val estimatedDepartureUtc: Instant?,
    @SerialName("at_platform")
    val atPlatform: Boolean?,
    @SerialName("platform_number")
    val platformNumber: String?,
    @SerialName("flags")
    val flags: String?,
    @SerialName("departure_sequence")
    val departureSequence: Int?
)

@Serializable
class PatternStops

@Serializable
class PatternRoutes

@Serializable
class PatternDirections

@Serializable
class PatternRuns

@Serializable
data class PatternsResponse(
    @SerialName("disruptions")
    val disruptions: List<StopDisruption>,
    @SerialName("departures")
    val departures: List<PatternDeparture>,
    @SerialName("stops")
    val stops: PatternStops,
    @SerialName("routes")
    val routes: PatternRoutes,
    @SerialName("runs")
    val runs: PatternRuns,
    @SerialName("directions")
    val directions: PatternDirections,
    @SerialName("status")
    val status: PtvApiStatus
)

enum class DepartureExpandOptions(val value: String) {
    ALL("All"),
    STOP("Stop"),
    ROUTE("Route"),
    RUN("Run"),
    DIRECTION("Direction"),
    DISRUPTION("Disruption"),
    VEHICLEPOSITION("VehiclePosition"),
    VEHICLEDESCRIPTOR("VehicleDescriptor"),
    NONE("None")
}

enum class RunExpandOptions(val value: String) {
    ALL("All"),
    VEHICLE_DESCRIPTOR("VehicleDescriptor"),
    VEHICLE_POSITION("VehiclePosition"),
    NONE("None")
}

enum class PatternExpandOptions(val value: String) {
    ALL("All"),
    STOP("Stop"),
    ROUTE("Route"),
    RUN("Run"),
    DIRECTION("Direction"),
    DISRUPTION("Disruption"),
    VEHICLE_POSITION("VehiclePosition"),
    VEHICLE_DESCRIPTOR("VehicleDescriptor"),
    NONE("None")
}

interface ApiClient {
    suspend fun getRouteTypes(): RouteTypeResponse
    suspend fun getRoutes(
        maybeRouteTypes: Option<List<RouteType>> = None,
        maybeRouteName: Option<String> = None
    ): RouteResponse

    suspend fun getStops(
        routeId: Int,
        routeType: Int,
        directionId: Option<Int> = None,
        stopDisruptions: Boolean = false,
        includeGeopath: Boolean = false,
        geopathUtc: Instant = Clock.System.now(),
    ): StopsResponse

    suspend fun getDirections(routeId: Int): DirectionsResponse
    suspend fun getDepartures(
        routeType: Int,
        stopId: Int,
        routeId: Int,
        directionId: Option<Int> = None,
        gtfs: Option<Boolean> = None,
        dateUtc: Option<Instant> = None,
        maxResults: Option<Int> = None,
        includeCancelled: Option<Boolean> = None,
        lookBackwards: Option<Boolean> = None,
        expand: Option<List<DepartureExpandOptions>> = None,
        includeGeopath: Option<Boolean> = None
    ): DeparturesResponse

    suspend fun getRuns(
        routeId: Int,
        routeType: Int,
        expand: Option<List<RunExpandOptions>> = None,
        dateUtc: Option<Instant> = None
    ): RunsResponse

    suspend fun getPatterns(
        runRef: String,
        routeType: Int,
        expand: Option<List<PatternExpandOptions>> = None,
        stopId: Option<Int> = None,
        dateUtc: Option<Instant> = None,
        includeSkippedStops: Option<Boolean> = None,
        includeGeopath: Option<Boolean> = None,
    ): PatternsResponse
}

class PtvApiClient(
    private val httpClient: HttpClient,
    ptvApiDevId: String,
    ptvApiKey: String
) : PtvClient(ptvApiDevId, ptvApiKey), ApiClient {
    override suspend fun getRouteTypes(): RouteTypeResponse {
        val routeTypesUrl = getUrl("/v3/route_types", mapOf())
        val response = httpClient.get {
            url(routeTypesUrl)
        }

        val responseBody = response.bodyAsText()

        try {
            return Json.decodeFromString<RouteTypeResponse>(responseBody)
        } catch (ex: Throwable) {
            println(responseBody)
            println(response.headers)
            throw ex
        }
    }

    override suspend fun getRoutes(
        maybeRouteTypes: Option<List<RouteType>>,
        maybeRouteName: Option<String>
    ): RouteResponse {
        val routesUrl = getUrl(
            "/v3/routes",
            mapOf(
                "route_types" to maybeRouteTypes.map { routeTypes -> routeTypes.map { routeType -> routeType.routeTypeId.toString() } },
                "route_name" to maybeRouteName.map { listOf(it) }
            )
        )
        val response = httpClient.get {
            url(routesUrl)
        }

        val responseBody = response.bodyAsText()

        try {
            return Json.decodeFromString<RouteResponse>(responseBody)
        } catch (ex: Throwable) {
            println(responseBody)
            println(response.headers)
            throw ex
        }
    }

    override suspend fun getStops(
        routeId: Int,
        routeType: Int,
        directionId: Option<Int>,
        stopDisruptions: Boolean,
        includeGeopath: Boolean,
        geopathUtc: Instant
    ): StopsResponse {
        val stopsUrl = getUrl(
            "/v3/stops/route/$routeId/route_type/$routeType",
            mapOf(
                "direction_id" to directionId.map { listOf(it.toString()) },
                "stop_disruptions" to Some(listOf(stopDisruptions.toString())),
                "include_geopath" to Some(listOf(includeGeopath.toString())),
                "geopath_utc" to Some(listOf(geopathUtc.toString()))
            )
        )
        val response = httpClient.get {
            url(stopsUrl)
        }

        val responseBody = response.bodyAsText()

        try {
            return Json.decodeFromString<StopsResponse>(responseBody)
        } catch (ex: Throwable) {
            println(responseBody)
            println(response.headers)
            throw ex
        }
    }

    override suspend fun getDirections(routeId: Int): DirectionsResponse {
        val directionsUrl = getUrl(
            "/v3/directions/route/$routeId", mapOf()
        )
        val response = httpClient.get {
            url(directionsUrl)
        }

        val responseBody = response.bodyAsText()

        try {
            return Json.decodeFromString<DirectionsResponse>(responseBody)
        } catch (ex: Throwable) {
            println(responseBody)
            println(response.headers)
            throw ex
        }
    }

    override suspend fun getDepartures(
        routeType: Int,
        stopId: Int,
        routeId: Int,
        directionId: Option<Int>,
        gtfs: Option<Boolean>,
        dateUtc: Option<Instant>,
        maxResults: Option<Int>,
        includeCancelled: Option<Boolean>,
        lookBackwards: Option<Boolean>,
        expand: Option<List<DepartureExpandOptions>>,
        includeGeopath: Option<Boolean>
    ): DeparturesResponse {
        val departuresUrl = getUrl(
            "/v3/departures/route_type/$routeType/stop/$stopId/route/$routeId",
            mapOf(
                "direction_id" to directionId.map { listOf(it.toString()) },
                "gtfs" to gtfs.map { listOf(it.toString()) },
                "date_utc" to dateUtc.map { listOf(it.toString()) },
                "max_results" to maxResults.map { listOf(it.toString()) },
                "include_cancelled" to includeCancelled.map { listOf(it.toString()) },
                "look_backwards" to lookBackwards.map { listOf(it.toString()) },
                "expand" to expand.map { expandOptions -> expandOptions.map { expandOption -> expandOption.value } },
                "include_geopath" to includeGeopath.map { listOf(it.toString()) },
            )
        )

        val response = httpClient.get {
            url(departuresUrl)
        }

        val responseBody = response.bodyAsText()

        try {
            return Json.decodeFromString<DeparturesResponse>(responseBody)
        } catch (ex: Throwable) {
            println(responseBody)
            println(response.headers)
            throw ex
        }
    }

    override suspend fun getRuns(
        routeId: Int,
        routeType: Int,
        expand: Option<List<RunExpandOptions>>,
        dateUtc: Option<Instant>
    ): RunsResponse {
        val runsUrl = getUrl(
            "/v3/runs/route/$routeId/route_type/$routeType",
            mapOf(
                "expand" to expand.map { options -> options.map { option -> option.value } },
                "date_utc" to dateUtc.map { listOf(it.toString()) },
            )
        )

        val response = httpClient.get {
            url(runsUrl)
        }

        val responseBody = response.bodyAsText()

        try {
            return Json.decodeFromString<RunsResponse>(responseBody)
        } catch (ex: Throwable) {
            println(responseBody)
            println(response.headers)
            throw ex
        }
    }

    override suspend fun getPatterns(
        runRef: String,
        routeType: Int,
        expand: Option<List<PatternExpandOptions>>,
        stopId: Option<Int>,
        dateUtc: Option<Instant>,
        includeSkippedStops: Option<Boolean>,
        includeGeopath: Option<Boolean>
    ): PatternsResponse {
        val runsUrl = getUrl(
            "/v3/pattern/run/$runRef/route_type/$routeType",
            mapOf(
                "expand" to expand.map { expandOpts -> expandOpts.map { expandOpt -> expandOpt.value } },
                "stop_id" to stopId.map { listOf(it.toString()) },
                "date_utc" to dateUtc.map { listOf(it.toString()) },
                "include_skipped_stops" to includeSkippedStops.map { listOf(it.toString()) },
                "include_geopath" to includeGeopath.map { listOf(it.toString()) }
            )
        )

        val response = httpClient.get {
            url(runsUrl)
        }

        val responseBody = response.bodyAsText()

        try {
            return Json.decodeFromString<PatternsResponse>(responseBody)
        } catch (ex: Throwable) {
            println(responseBody)
            println(response.headers)
            throw ex
        }
    }
}