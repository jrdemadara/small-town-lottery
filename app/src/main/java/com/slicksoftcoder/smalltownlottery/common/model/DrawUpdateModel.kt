package com.slicksoftcoder.smalltownlottery.common.model

data class DrawUpdateModel(
    val serial: String,
    val drawName: String,
    val drawTime: String,
    val cutoff: String,
    val resume: String
)
