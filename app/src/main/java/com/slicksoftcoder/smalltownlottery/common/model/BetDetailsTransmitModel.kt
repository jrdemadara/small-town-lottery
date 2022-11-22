package com.slicksoftcoder.smalltownlottery.common.model

data class BetDetailsTransmitModel(
    val serial: String,
    val headerSerial: String,
    val betNumber: String,
    val amount: String,
    val win: String,
    val isRambolito: String
)
