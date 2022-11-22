package com.slicksoftcoder.smalltownlottery.features.dashboard

import java.sql.DataTruncation

data class Draw2pmModel(
    val result: String,
    val totalBet: String,
    val totalHit: String,
    val pnl: String,
    val win: String
)
