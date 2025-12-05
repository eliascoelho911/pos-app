package com.eliascoelho911.paymentsdk.gateway

import com.eliascoelho911.paymentsdk.external.hardware.PrinterWriter

class PrinterGateway(
    private val printerWriter: PrinterWriter
) {
    suspend fun print(text: String) {
        printerWriter.print(text)
    }

    suspend fun println(text: String) {
        printerWriter.println(text)
    }

    suspend fun printLine() {
        printerWriter.printLine()
    }
}
