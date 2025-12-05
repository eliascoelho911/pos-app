package com.eliascoelho911.paymentsdk.api

import com.eliascoelho911.paymentsdk.external.client.sandbox.SandboxPaymentClient
import com.eliascoelho911.paymentsdk.external.hardware.FakeCardReader
import com.eliascoelho911.paymentsdk.external.hardware.FakePinReader
import com.eliascoelho911.paymentsdk.gateway.PaymentGateway
import com.eliascoelho911.paymentsdk.model.PaymentEvent
import com.eliascoelho911.paymentsdk.model.PaymentRequest
import com.eliascoelho911.paymentsdk.service.SandboxPaymentFacade
import kotlinx.coroutines.flow.Flow

interface PaymentFacade {
    fun startPayment(request: PaymentRequest): Flow<PaymentEvent>

    companion object {
        fun sandbox(): PaymentFacade = SandboxPaymentFacade(
            gateway = PaymentGateway(
                paymentClient = SandboxPaymentClient()
            ),
            cardReader = FakeCardReader(),
            pinReader = FakePinReader()
        )
    }
}