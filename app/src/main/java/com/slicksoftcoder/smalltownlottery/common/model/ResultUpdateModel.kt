package com.slicksoftcoder.smalltownlottery.common.model

data class ResultUpdateModel(
    val serial: String,
    val drawSerial: String,
    val drawDate: String,
    val winningNumber: String,
    val dateCreated: String,
)
