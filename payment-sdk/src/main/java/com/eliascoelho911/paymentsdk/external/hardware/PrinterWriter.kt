package com.eliascoelho911.paymentsdk.external.hardware

import kotlinx.coroutines.delay

interface PrinterWriter {
    suspend fun print(text: String)
    suspend fun println(text: String)
    suspend fun printLine()

    companion object {
        fun console(): PrinterWriter = ConsolePrinterWriter()
    }
}

internal class ConsolePrinterWriter(
    val delay: Long = 250L,
) : PrinterWriter {
    override suspend fun print(text: String) {
        delay(delay)
        kotlin.io.print("PRINTER: ")
        kotlin.io.print(text)
    }

    override suspend fun println(text: String) {
        delay(delay)
        kotlin.io.print("PRINTER: ")
        kotlin.io.println(text)
    }

    override suspend fun printLine() {
        kotlin.io.println("--------------------------------------------------")
    }
}
