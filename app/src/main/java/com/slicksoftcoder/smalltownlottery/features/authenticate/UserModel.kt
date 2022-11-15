package com.slicksoftcoder.smalltownlottery.features.authenticate

data class UserModel(
    val username: String,
    val password: String,
    val deviceid: String
)
