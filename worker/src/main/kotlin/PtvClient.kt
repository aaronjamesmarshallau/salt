package me.i18u

import arrow.core.*
import io.ktor.http.*
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

abstract class PtvClient(
    private val ptvApiDevId: String,
    private val ptvApiKey: String
) {
    fun getUrl(slug: String, parameters: Map<String, Option<List<String>>>): String {
        val urlBuilder = URLBuilder(
            host = "timetableapi.ptv.vic.gov.au",
            protocol = URLProtocol.HTTPS,
            pathSegments = slug.split("/"),
            parameters = parametersOf(
                parameters.mapNotNull { it.value.getOrNull() }
            ).plus(
                parametersOf("devid", ptvApiDevId)
            )
        )

        val pathWithoutSig = urlBuilder.build().encodedPathAndQuery
        val signingKey = SecretKeySpec(ptvApiKey.toByteArray(Charsets.US_ASCII), "HmacSHA1")
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(signingKey)
        val unencodedSignature = mac.doFinal(pathWithoutSig.toByteArray(Charsets.US_ASCII))
        val encodedSignature = unencodedSignature.joinToString("") {
            String.format("%02x", it)
        }

        urlBuilder.parameters.append("signature", encodedSignature)

        return urlBuilder.buildString()
    }
}
