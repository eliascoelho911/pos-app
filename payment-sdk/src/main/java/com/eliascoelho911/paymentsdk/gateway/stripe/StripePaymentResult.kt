package com.eliascoelho911.paymentsdk.gateway.stripe

import kotlinx.serialization.Serializable

@Serializable
data class StripePaymentResult(
    val id: String,
    val currency: String,
    val amount: Long,
    val paymentMethod: String? = null,
    val status: Status,
    val created: Long
) {
    @Serializable
    enum class Status {
        /**
         * The PaymentIntent has been canceled.
         */
        CANCELED,

        /**
         * The PaymentIntent is currently being processed.
         */
        PROCESSING,

        /**
         * The PaymentIntent requires additional action from the customer.
         */
        REQUIRES_ACTION,

        /**
         * The PaymentIntent has been confirmed and requires capture.
         */
        REQUIRES_CAPTURE,

        /**
         * The PaymentIntent requires confirmation.
         */
        REQUIRES_CONFIRMATION,

        /**
         * The PaymentIntent requires a payment method to be attached.
         */
        REQUIRES_PAYMENT_METHOD,

        /**
         * The PaymentIntent has succeeded.
         */
        SUCCEEDED
    }
}
