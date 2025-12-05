package com.eliascoelho911.paymentsdk.domain

import com.eliascoelho911.paymentsdk.api.PaymentClient
import com.eliascoelho911.paymentsdk.api.PaymentEvent
import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.device.DeviceInteractor
import com.eliascoelho911.paymentsdk.gateway.PaymentGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PaymentClientImpl(
    private val gateway: PaymentGateway,
    private val deviceInteractor: DeviceInteractor
) : PaymentClient {
    override suspend fun startPayment(request: PaymentRequest): Flow<PaymentEvent> = flow {
        emit(PaymentEvent.WaitingForCard)

        val card = deviceInteractor.waitAndReadCard()
        emit(PaymentEvent.Processing)

        val paymentResult = gateway.processPayment(request, card.payload)

        emit(PaymentEvent.Finished(paymentResult))
    }.flowOn(Dispatchers.Default)
}