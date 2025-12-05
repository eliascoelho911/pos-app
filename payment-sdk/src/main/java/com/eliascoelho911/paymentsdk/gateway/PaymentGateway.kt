package com.eliascoelho911.paymentsdk.gateway

import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.api.PaymentStatus
import com.eliascoelho911.paymentsdk.domain.model.CardPayload

interface PaymentGateway {
    suspend fun processPayment(request: PaymentRequest, payload: CardPayload): PaymentStatus
}
