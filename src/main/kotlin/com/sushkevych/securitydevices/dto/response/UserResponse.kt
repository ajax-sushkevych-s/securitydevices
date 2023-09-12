package com.sushkevych.securitydevices.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.sushkevych.securitydevices.model.MongoUser

data class UserResponse(
    val id: String?,
    val username: String?,
    val email: String?,
    @field:JsonProperty(value = "mobile_number")
    val mobileNumber: String?,
    val devices: List<MongoUser.MongoUserDevice>?
)

fun MongoUser.toResponse() = UserResponse(
    id = id?.toHexString(),
    username = username,
    email = email,
    mobileNumber = mobileNumber,
    devices = devices
)
