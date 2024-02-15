package me.i18u

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

fun main() {
    val client = HttpClient(CIO)
}