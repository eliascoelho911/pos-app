package com.eliascoelho911.paymentsdk.device

import com.eliascoelho911.paymentsdk.domain.model.CardDisplayInfo
import com.eliascoelho911.paymentsdk.domain.model.CardPayload

data class DeviceCard(
    val payload: CardPayload,
    val displayInfo: CardDisplayInfo
)
