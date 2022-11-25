package com.slicksoftcoder.smalltownlottery.common.model

data class UserUpdateModel(
    val serial: String,
    val agentSerial: String,
    val username: String,
    val password: String,
    val deviceId: String,
    val location: String
)
