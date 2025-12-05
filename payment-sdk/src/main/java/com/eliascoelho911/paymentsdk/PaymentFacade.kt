package com.eliascoelho911.paymentsdk

import com.eliascoelho911.paymentsdk.external.client.sandbox.SandboxPaymentClient
import com.eliascoelho911.paymentsdk.external.hardware.FakeCardReader
import com.eliascoelho911.paymentsdk.external.hardware.FakePinReader
import com.eliascoelho911.paymentsdk.gateway.PaymentGateway
import com.eliascoelho911.paymentsdk.model.Card
import com.eliascoelho911.paymentsdk.service.PaymentService
import com.eliascoelho911.paymentsdk.service.SandboxPaymentService

interface PaymentFacade : PaymentService {
    companion object {
        fun create(card: () -> Card, pin: () -> String): PaymentFacade {
            return PaymentFacadeImpl(
                paymentService = SandboxPaymentService(
                    gateway = PaymentGateway(
                        paymentClient = SandboxPaymentClient()
                    ),
                    cardReader = FakeCardReader(card()),
                    pinReader = FakePinReader(pin()),
                ),
            )
        }
    }
}

internal class PaymentFacadeImpl(
    private val paymentService: PaymentService,
) : PaymentFacade,
    PaymentService by paymentService
