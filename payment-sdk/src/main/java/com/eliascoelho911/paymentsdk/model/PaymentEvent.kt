package com.eliascoelho911.paymentsdk.model

sealed class PaymentEvent {
    data object WaitingForCard: PaymentEvent()
    data object WaitingForPin: PaymentEvent()
    data object Processing: PaymentEvent()
    data class Finished(val status: PaymentStatus): PaymentEvent()
}