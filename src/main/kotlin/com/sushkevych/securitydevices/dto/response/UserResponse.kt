package com.sushkevych.securitydevices.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.sushkevych.securitydevices.model.MongoUser

data class UserResponse(
    val id: String?,
    val username: String?,
    val email: String?,
    @field:JsonProperty(value = "mobile_number")
    val mobileNumber: String?,
    val devices: List<UserDeviceResponse?>
)

data class UserDeviceResponse(
    @field:JsonProperty(value = "device_id")
    val deviceId: String?,
    @field:JsonProperty(value = "user_device_id")
    val userDeviceId: String?,
    val role: MongoUser.MongoUserRole?
)

fun MongoUser.toResponse() = UserResponse(
    id = id?.toHexString(),
    username = username,
    email = email,
    mobileNumber = mobileNumber,
    devices = devices.map { it?.toResponse() }
)

fun MongoUser.MongoUserDevice.toResponse() = UserDeviceResponse(
    deviceId = deviceId?.toHexString(),
    userDeviceId = userDeviceId?.toHexString(),
    role = role
)
