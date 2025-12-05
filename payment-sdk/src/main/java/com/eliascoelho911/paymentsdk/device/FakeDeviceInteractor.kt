package com.eliascoelho911.paymentsdk.device

import kotlinx.coroutines.delay

class FakeDeviceInteractor(
    private val card: DeviceCard,
    val delay: Long = 500L,
): DeviceInteractor {
    override suspend fun waitAndReadCard(): DeviceCard {
        delay(delay)
        return card
    }
}