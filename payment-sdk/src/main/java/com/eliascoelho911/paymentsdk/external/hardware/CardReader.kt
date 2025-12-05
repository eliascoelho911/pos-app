package com.eliascoelho911.paymentsdk.external.hardware

import com.eliascoelho911.paymentsdk.model.Card
import kotlinx.coroutines.delay

interface CardReader {
    suspend fun waitAndReadCard(): Card
}

internal class FakeCardReader(
    private val card: Card,
    val delay: Long = 2000L,
): CardReader {
    override suspend fun waitAndReadCard(): Card {
        delay(delay)
        return card
    }
}