package com.slicksoftcoder.smalltownlottery.common.model

data class BetHeaderTransmitModel(
    val serial: String,
    val agent: String,
    val drawDate: String,
    val drawSerial: String,
    val transactionCode: String,
    val totalAmount: String,
    val dateCreated: String,
    val datePrinted: String,
    val isVoid: String,
    val editedBy: String,
    val dateEdited: String
)
