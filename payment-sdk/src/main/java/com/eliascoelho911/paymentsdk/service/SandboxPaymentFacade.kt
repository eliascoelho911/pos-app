package com.eliascoelho911.paymentsdk.service

import com.eliascoelho911.paymentsdk.api.PaymentFacade
import com.eliascoelho911.paymentsdk.external.hardware.CardReader
import com.eliascoelho911.paymentsdk.external.hardware.PinReader
import com.eliascoelho911.paymentsdk.gateway.PaymentGateway
import com.eliascoelho911.paymentsdk.model.PaymentEvent
import com.eliascoelho911.paymentsdk.model.PaymentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class SandboxPaymentFacade(
    private val gateway: PaymentGateway,
    private val cardReader: CardReader,
    private val pinReader: PinReader
) : PaymentFacade {
    override fun startPayment(request: PaymentRequest): Flow<PaymentEvent> = flow {
        emit(PaymentEvent.WaitingForCard)

        val card = cardReader.waitAndReadCard()
        emit(PaymentEvent.CardRead(card))

        emit(PaymentEvent.WaitingForPin)
        val pin = pinReader.waitAndReadPin()
        emit(PaymentEvent.PinCollected(pin))

        emit(PaymentEvent.Processing)
        val paymentResult = gateway.processPayment(request, card.payload)

        emit(PaymentEvent.Finished(paymentResult))
    }
}
