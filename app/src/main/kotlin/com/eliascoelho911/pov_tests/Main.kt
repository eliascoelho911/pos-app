package com.eliascoelho911.pov_tests

import com.eliascoelho911.paymentsdk.api.PaymentEvent
import com.eliascoelho911.paymentsdk.api.PaymentMethod
import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.api.PaymentStatus
import com.eliascoelho911.paymentsdk.device.DeviceCard
import com.eliascoelho911.paymentsdk.device.FakeDeviceInteractor
import com.eliascoelho911.paymentsdk.domain.PaymentClientImpl
import com.eliascoelho911.paymentsdk.domain.model.CardDisplayInfo
import com.eliascoelho911.paymentsdk.domain.model.CardPayload
import com.eliascoelho911.paymentsdk.gateway.sandbox.SandboxPaymentGateway
import com.eliascoelho911.paymentsdk.gateway.stripe.StripeClient
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    val amount = args.getOrNull(0)?.toLongOrNull()?.takeIf { it > 0 } ?: 1000L
    val method = args.getOrNull(1)
        ?.uppercase()
        ?.let { runCatching { PaymentMethod.valueOf(it) }.getOrNull() }
        ?: PaymentMethod.CREDIT
    val installments = args.getOrNull(2)?.toIntOrNull()?.takeIf { it > 0 } ?: 1

    val request = PaymentRequest(
        amountCents = amount,
        method = method,
        installments = installments,
        description = "CLI payment"
    )

    val deviceInteractor = FakeDeviceInteractor(
        card = DeviceCard(
            payload = CardPayload(
                cardNumber = "4242424242424242",
                cardHolderName = "CLI User",
                expirationDate = "12/34",
                cvv = "123"
            ),
            displayInfo = CardDisplayInfo(
                maskedPan = "**** **** **** 4242",
                brand = "VISA"
            )
        ),
        delay = 500L
    )

    val paymentClient = PaymentClientImpl(
        gateway = SandboxPaymentGateway(StripeClient()),
        deviceInteractor = deviceInteractor
    )

    println("Iniciando pagamento de ${request.amountCents} centavos via ${request.method} em ${request.installments}x\n")

    paymentClient.startPayment(request).collect { event ->
        when (event) {
            PaymentEvent.WaitingForCard -> println("Aguardando cartão...")
            PaymentEvent.Processing -> println("Processando pagamento...")
            is PaymentEvent.Finished -> printStatus(event.status)
        }
    }
}

private fun printStatus(status: PaymentStatus) {
    when (status) {
        is PaymentStatus.Approved -> println(
            buildString {
                appendLine("Pagamento aprovado!")
                appendLine("ID: ${status.id}")
                appendLine("Bandeira: ${status.brand ?: "desconhecida"}")
                appendLine("Cartão: ${status.maskedCard ?: "não informado"}")
                appendLine("Criado em: ${status.createdAt}")
            }
        )

        is PaymentStatus.Declined -> println(
            buildString {
                appendLine("Pagamento recusado: ${status.reason}")
                status.message?.let { appendLine("Motivo: $it") }
            }
        )

        is PaymentStatus.Error -> println(
            buildString {
                appendLine("Erro no pagamento")
                status.message?.let { appendLine(it) }
                status.cause?.let { appendLine(it.stackTraceToString()) }
            }
        )
    }
}
