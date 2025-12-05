plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "com.eliascoelho911.pov_tests.MainKt"
}

dependencies {
    implementation(project(":payment-sdk"))
    implementation(libs.kotlinx.coroutines.core)
}
