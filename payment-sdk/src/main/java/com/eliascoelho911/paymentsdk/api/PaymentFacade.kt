package com.eliascoelho911.paymentsdk.api

import kotlinx.coroutines.flow.Flow

interface PaymentFacade {
    suspend fun startPayment(request: PaymentRequest): Flow<PaymentEvent>
}

sealed class PaymentEvent {
    data object WaitingForCard: PaymentEvent()
    data object Processing: PaymentEvent()
    data class Finished(val status: PaymentStatus): PaymentEvent()
}