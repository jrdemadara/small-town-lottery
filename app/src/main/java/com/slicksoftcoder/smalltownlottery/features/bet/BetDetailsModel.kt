package com.slicksoftcoder.smalltownlottery.features.bet

data class BetDetailsModel(
    val serial: String,
    val betNumber: String,
    val amount: String,
    val win: String,
    val isRambolito: String,
    val isLowWin: String
)
