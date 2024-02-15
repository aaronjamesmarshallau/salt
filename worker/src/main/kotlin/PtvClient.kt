package me.i18u

import io.ktor.http.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

abstract class PtvClient {
    fun getUrl(slug: String, parameters: Map<String, List<String>>): String {
        val urlBuilder = URLBuilder(
            host = "timetableapi.ptv.vic.gov.au",
            protocol = URLProtocol.HTTPS,
            pathSegments = slug.split("/"),
            parameters = parametersOf(parameters).plus(parametersOf("devid", "example"))
        )

        val urlWithoutSig = urlBuilder.buildString()
        val key = "example"
        val signingKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA1")
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(signingKey)
        val unencodedSignature = mac.doFinal(urlWithoutSig.toByteArray(Charsets.UTF_8))
        val encodedSignature = unencodedSignature.joinToString("") {
            String.format("%02x", it)
        }
        
        urlBuilder.parameters.append("signature", encodedSignature)
        
        return urlBuilder.buildString()
    }
}
