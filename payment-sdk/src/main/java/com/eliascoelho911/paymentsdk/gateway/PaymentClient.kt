package com.eliascoelho911.paymentsdk.gateway

import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.api.PaymentStatus
import com.eliascoelho911.paymentsdk.domain.model.CardPayload

interface PaymentClient {
    suspend fun processPayment(
        paymentRequest: PaymentRequest,
        cardPayload: CardPayload
    ): PaymentStatus
}