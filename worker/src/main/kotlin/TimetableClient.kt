package me.i18u

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteType(
    @SerialName("route_type_name")
    val routeTypeName: String,
    @SerialName("route_type")
    val routeTypeId: Int
)

interface ApiClient {
    fun getRouteTypes(): Deferred<List<RouteType>>
}

class PtvApiClient(val httpClient: HttpClient) : PtvClient(), ApiClient {
    fun test() {
        val x = async {
            httpClient.request(
                "test"
            ) {
                method = HttpMethod.Get
            }
        }
    }
}