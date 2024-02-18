val ktorversion: String by project
val kotlinxversion: String by project
val kotlinxdatetimeversion: String by project
val arrowversion: String by project
val awsversion: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

application {
    mainClass = "me.i18u.MainKt"
}

group = "me.i18u"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorversion")
    implementation("io.ktor:ktor-client-cio:$ktorversion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxversion")
    implementation("io.arrow-kt:arrow-core:$arrowversion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxdatetimeversion")
    implementation("aws.sdk.kotlin:aws-core:$awsversion")
    implementation("aws.sdk.kotlin:dynamodb:$awsversion")


    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}