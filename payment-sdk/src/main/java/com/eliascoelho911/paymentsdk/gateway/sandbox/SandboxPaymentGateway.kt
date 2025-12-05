package com.eliascoelho911.paymentsdk.gateway.sandbox

import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.api.PaymentStatus
import com.eliascoelho911.paymentsdk.domain.model.CardPayload
import com.eliascoelho911.paymentsdk.gateway.PaymentGateway
import com.eliascoelho911.paymentsdk.gateway.stripe.StripeClient
import com.eliascoelho911.paymentsdk.gateway.stripe.mapper.getPaymentStatus

class SandboxPaymentGateway(
    private val client: StripeClient
) : PaymentGateway {
    override suspend fun processPayment(
        request: PaymentRequest,
        payload: CardPayload
    ): PaymentStatus {
        val paymentResult = client.processPayment(
            paymentRequest = request,
            cardPayload = payload
        )

        return paymentResult.getPaymentStatus()
    }
}