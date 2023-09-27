package com.sushkevych.securitydevices.dto.response

import com.sushkevych.securitydevices.model.MongoUser

data class UserResponse(
    val id: String?,
    val username: String?,
    val email: String?,
    val mobileNumber: String?,
    val devices: List<UserDeviceResponse?>
)

data class UserDeviceResponse(
    val deviceId: String?,
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
