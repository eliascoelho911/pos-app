package com.eliascoelho911.paymentsdk.model

data class PaymentRequest(
    val amountCents: Long,
    val currency: Currency = Currency.BRL,
    val method: PaymentMethod,
    val installments: Int = 1,
    val description: String? = null,
    var pin: String? = null,
)

enum class PaymentMethod {
    CREDIT, DEBIT;
}

enum class Currency {
    BRL;
}