package com.sushkevych.securitydevices.dto.request

import com.sushkevych.securitydevices.model.MongoUser
import jakarta.validation.constraints.NotEmpty
import org.bson.types.ObjectId

data class UserRequest(
    val id: String?,
    @field:NotEmpty(message = "Username cannot be empty.")
    val username: String,
    @field:NotEmpty(message = "Email cannot be empty.")
    val email: String,
    @field:NotEmpty(message = "Mobile number cannot be empty.")
    val mobileNumber: String,
    val password: String,
    val devices: List<UserDeviceRequest>
)

data class UserDeviceRequest(
    val deviceId: String?,
    val userDeviceId: String?,
    val role: MongoUser.MongoUserRole?
)

fun UserRequest.toEntity() = MongoUser(
    id = id?.let { ObjectId(id) },
    username = username,
    email = email,
    mobileNumber = mobileNumber,
    password = password,
    devices = devices.map { it.toEntity() }
)

fun UserDeviceRequest.toEntity() = MongoUser.MongoUserDevice(
    deviceId = ObjectId(deviceId),
    userDeviceId = ObjectId(userDeviceId),
    role = role
)
