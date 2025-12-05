package com.eliascoelho911.pov_tests

import com.eliascoelho911.paymentsdk.api.PaymentFacade
import com.eliascoelho911.paymentsdk.external.hardware.PrinterWriter
import com.eliascoelho911.paymentsdk.gateway.PrinterGateway
import com.eliascoelho911.paymentsdk.model.PaymentEvent
import com.eliascoelho911.paymentsdk.model.PaymentMethod
import com.eliascoelho911.paymentsdk.model.PaymentRequest
import com.eliascoelho911.paymentsdk.model.PaymentStatus
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import kotlinx.coroutines.runBlocking

private data class PaymentCliConfig(
    val amount: Long,
    val method: PaymentMethod,
    val installments: Int
)

private val printerWriter = PrinterGateway(PrinterWriter.console())
private fun println(message: String) = runBlocking { printerWriter.println(message) }
private fun printLine() = runBlocking { printerWriter.printLine() }

private val paymentFacade = PaymentFacade.sandbox()

fun main(args: Array<String>) = runBlocking {
    val cliConfig = parseArguments(args)
    val request = PaymentRequest(
        amountCents = cliConfig.amount,
        method = cliConfig.method,
        installments = cliConfig.installments,
        description = "CLI payment"
    )

    println("Iniciando pagamento de ${request.amountCents} centavos via ${request.method} em ${request.installments}x")
    printLine()

    paymentFacade.startPayment(request).collect { event ->
        when (event) {
            PaymentEvent.WaitingForCard -> println("Aguardando cartão...")
            PaymentEvent.WaitingForPin -> println("Aguardando PIN...")
            is PaymentEvent.CardRead -> println("Cartão lido: ${event.card.displayInfo.maskedPan} (${event.card.displayInfo.brand})")
            is PaymentEvent.PinCollected -> println("PIN capturado com sucesso.")
            PaymentEvent.Processing -> println("Processando pagamento...")
            is PaymentEvent.Finished -> printStatus(event.status)
        }
    }

    printLine()
}

private const val DEFAULT_AMOUNT = 1000L
private const val DEFAULT_INSTALLMENTS = 1

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

private fun parseArguments(args: Array<String>): PaymentCliConfig {
    val parser = ArgParser("povtests")
    val amount by parser.argument(ArgType.String, "amount", "Valor em centavos").optional()
    val method by parser.argument(ArgType.String, "method", "Método de pagamento").optional()
    val installments by parser.argument(ArgType.String, "installments", "Número de parcelas").optional()

    parser.parse(args)

    return PaymentCliConfig(
        amount = amount?.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_AMOUNT,
        method = method
            ?.uppercase()
            ?.let { runCatching { PaymentMethod.valueOf(it) }.getOrNull() }
            ?: PaymentMethod.CREDIT,
        installments = installments?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_INSTALLMENTS
    )
}
