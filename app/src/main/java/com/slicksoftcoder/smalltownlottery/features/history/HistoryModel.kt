package com.slicksoftcoder.smalltownlottery.features.history

data class HistoryModel(
    val headerSerial: String,
    val drawDate: String,
    val drawTime: String,
    val transactionCode: String,
    val totalAmount: String,
    val isVoid: String,
    val betTime: String,
)
