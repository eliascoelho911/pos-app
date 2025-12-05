package com.eliascoelho911.paymentsdk.gateway.sandbox

import com.eliascoelho911.paymentsdk.api.DeclineReason
import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.api.PaymentStatus
import com.eliascoelho911.paymentsdk.domain.model.CardPayload
import com.eliascoelho911.paymentsdk.gateway.PaymentClient
import kotlinx.coroutines.delay

class SandboxPaymentClient(
    private val processingDelayMillis: Long = 200L,
    private val timestampProvider: () -> Long = System::currentTimeMillis
) : PaymentClient {
    override suspend fun processPayment(
        paymentRequest: PaymentRequest,
        cardPayload: CardPayload
    ): PaymentStatus {
        delay(processingDelayMillis)

        if (paymentRequest.amountCents <= 0) {
            return PaymentStatus.Error("Amount must be greater than zero.")
        }

        if (paymentRequest.installments <= 0) {
            return PaymentStatus.Error("Installments must be greater than zero.")
        }

        val digitsOnlyCard = cardPayload.cardNumber.filter(Char::isDigit)
        if (digitsOnlyCard.isEmpty()) {
            return PaymentStatus.Error("Invalid card payload: missing digits.")
        }

        if (paymentRequest.amountCents > MAX_AMOUNT_CENTS) {
            return PaymentStatus.Declined(
                reason = DeclineReason.OTHER,
                message = "Sandbox limit is $MAX_AMOUNT_CENTS cents per transaction."
            )
        }

        val createdAt = timestampProvider()
        val decisionDigit = digitsOnlyCard.last()
        return when (decisionDigit) {
            '1' -> PaymentStatus.Declined(
                reason = DeclineReason.INSUFFICIENT_FUNDS,
                message = "Sandbox rule: cards ending in 1 have insufficient funds."
            )

            '3' -> PaymentStatus.Declined(
                reason = DeclineReason.CARD_EXPIRED,
                message = "Sandbox rule: cards ending in 3 are expired."
            )

            '4' -> PaymentStatus.Declined(
                reason = DeclineReason.INVALID_CARD,
                message = "Sandbox rule: cards ending in 4 are invalid."
            )

            '5' -> PaymentStatus.Declined(
                reason = DeclineReason.SUSPECTED_FRAUD,
                message = "Sandbox rule: cards ending in 5 are flagged for review."
            )

            '6' -> PaymentStatus.Error("Sandbox rule: cards ending in 6 trigger a gateway error.")

            else -> PaymentStatus.Approved(
                id = buildPaymentId(paymentRequest, digitsOnlyCard, createdAt),
                maskedCard = maskCard(digitsOnlyCard),
                brand = detectBrand(digitsOnlyCard),
                createdAt = createdAt
            )
        }
    }

    private fun buildPaymentId(
        paymentRequest: PaymentRequest,
        cardDigits: String,
        createdAt: Long
    ): String = buildString {
        append("sandbox-")
        append(paymentRequest.method.name.lowercase())
        append('-')
        append(paymentRequest.installments)
        append('-')
        append(cardDigits.takeLast(4))
        append('-')
        append(createdAt)
    }

    private fun maskCard(cardDigits: String): String {
        if (cardDigits.length <= 4) return cardDigits
        val masked = cardDigits.mapIndexed { index, c ->
            if (index < cardDigits.length - 4) '*' else c
        }.joinToString("")
        return masked.chunked(4).joinToString(" ").trim()
    }

    private fun detectBrand(cardDigits: String): String? = when {
        cardDigits.startsWith("4") -> "VISA"
        cardDigits.startsWith("5") -> "MASTERCARD"
        cardDigits.startsWith("3") -> "AMEX"
        cardDigits.startsWith("6") -> "DISCOVER"
        else -> null
    }

    private companion object {
        private const val MAX_AMOUNT_CENTS = 500_000L
    }
}
