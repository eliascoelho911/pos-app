package com.eliascoelho911.paymentsdk.domain.model

data class CardDisplayInfo(
    val maskedPan: String,
    val brand: String
)

data class CardPayload(
    val cardNumber: String,
    val cardHolderName: String,
    val expirationDate: String,
    val cvv: String
)
