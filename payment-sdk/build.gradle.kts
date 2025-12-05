import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(21)
}

val localProperties by lazy {
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { load(it) }
        }
    }
}

fun getConfigProperty(key: String): String? =
    providers.gradleProperty(key).orNull
        ?: providers.environmentVariable(key).orNull
        ?: localProperties.getProperty(key)

val stripeDefaultCustomerId = getConfigProperty("STRIPE_DEFAULT_CUSTOMER_ID")
    ?: error("Define STRIPE_DEFAULT_CUSTOMER_ID via gradle.properties, local.properties, or environment variables")
val stripeSecretKey = getConfigProperty("STRIPE_SECRET_KEY")
    ?: error("Define STRIPE_SECRET_KEY via gradle.properties, local.properties, or environment variables")

fun String.escapeForKotlinLiteral(): String =
    replace("\\", "\\\\").replace("\"", "\\\"")

val generatedDir = layout.buildDirectory.dir("generated/sources/buildConfig/kotlin")

val generateBuildConfig = tasks.register("generateBuildConfig") {
    val outputDir = generatedDir.get().asFile
    outputs.dir(outputDir)

    doLast {
        val packageDir = outputDir.resolve("com/eliascoelho911/paymentsdk")
        packageDir.mkdirs()
        packageDir.resolve("BuildConfig.kt").writeText(
            """
            package com.eliascoelho911.paymentsdk

            object BuildConfig {
                const val STRIPE_DEFAULT_CUSTOMER_ID: String = "${stripeDefaultCustomerId.escapeForKotlinLiteral()}"
                const val STRIPE_SECRET_KEY: String = "${stripeSecretKey.escapeForKotlinLiteral()}"
            }
            """.trimIndent()
        )
    }
}

sourceSets.named("main") {
    kotlin.srcDir(generatedDir)
}

tasks.named("compileKotlin") {
    dependsOn(generateBuildConfig)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(kotlin("test"))
}
