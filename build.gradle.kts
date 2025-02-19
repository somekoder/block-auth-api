
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.somekoder.block.auth.api"
version = providers.gradleProperty("AUTH_VERSION")

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)

    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.postgresql)

    // Hashing
    implementation(libs.jbcrypt)
}

ktor {
    docker {
        localImageName.set(provider { "block-auth-api" })
        jreVersion.set(JavaVersion.VERSION_21)
        imageTag.set(providers.environmentVariable("DOCKER_IMAGE_VERSION"))
        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.externalRegistry(
                project = providers.environmentVariable("DOCKER_PROJECT_NAME"),
                username = providers.environmentVariable("DOCKER_USERNAME"),
                password = providers.environmentVariable("DOCKER_PASSWORD"),
                hostname = providers.environmentVariable("DOCKER_HOST"),
            )
        )
    }
}
