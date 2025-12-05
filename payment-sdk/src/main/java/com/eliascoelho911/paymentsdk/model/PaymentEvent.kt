package com.eliascoelho911.paymentsdk.model

sealed class PaymentEvent {
    data object WaitingForCard: PaymentEvent()
    data object WaitingForPin: PaymentEvent()
    data class CardRead(val card: Card) : PaymentEvent()
    data class PinCollected(val pin: String) : PaymentEvent()
    data object Processing: PaymentEvent()
    data class Finished(val status: PaymentStatus): PaymentEvent()
}
