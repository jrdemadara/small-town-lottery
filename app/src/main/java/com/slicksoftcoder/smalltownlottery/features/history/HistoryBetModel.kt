package com.slicksoftcoder.smalltownlottery.features.history

data class HistoryBetModel(
    val serial: String,
    val headerSerial: String,
    val betNumber: String,
    val amount: String,
    val win: String,
    val isRambolito: String,
    val isLowWin: String
)
