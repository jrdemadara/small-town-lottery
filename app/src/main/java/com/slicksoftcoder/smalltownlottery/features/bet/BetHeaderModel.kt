package com.slicksoftcoder.smalltownlottery.features.bet

data class BetHeaderModel(
    val serial: String,
    val agent: String,
    val drawDate: String,
    val transactionCode: String,
    val totalAmount: String,
    val datePrinted: String,
    val isVoid: String,
)
