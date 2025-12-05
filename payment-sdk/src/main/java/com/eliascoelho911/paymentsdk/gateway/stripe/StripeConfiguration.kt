package com.eliascoelho911.paymentsdk.gateway.stripe

import java.util.Properties

data class StripeConfiguration(
    val secretKey: String,
    val defaultCustomerId: String,
) {
    companion object {
        fun fromEnvironment(
            environment: Map<String, String> = System.getenv(),
            systemProperties: Properties = System.getProperties()
        ): StripeConfiguration {
            fun resolve(key: String): String? =
                environment[key] ?: systemProperties.getProperty(key)

            val secretKey = resolve("STRIPE_SECRET_KEY")
                ?: error("Define STRIPE_SECRET_KEY via environment variables or system properties.")
            val defaultCustomerId = resolve("STRIPE_DEFAULT_CUSTOMER_ID")
                ?: error("Define STRIPE_DEFAULT_CUSTOMER_ID via environment variables or system properties.")

            return StripeConfiguration(
                secretKey = secretKey,
                defaultCustomerId = defaultCustomerId
            )
        }
    }
}
