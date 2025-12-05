package com.eliascoelho911.paymentsdk.device

interface DeviceInteractor {
    suspend fun waitAndReadCard(): DeviceCard
}