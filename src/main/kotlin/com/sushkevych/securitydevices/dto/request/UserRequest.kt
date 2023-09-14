package com.sushkevych.securitydevices.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
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
    @field:JsonProperty(value = "mobile_number")
    val mobileNumber: String,
    val password: String,
    val devices: List<UserDeviceRequest>
)

data class UserDeviceRequest(
    @field:JsonProperty(value = "device_id")
    val deviceId: String?,
    @field:JsonProperty(value = "user_device_id")
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

fun UserDeviceRequest.toEntity() = MongoUser.MongoUserDevice (
    deviceId = ObjectId(deviceId),
    userDeviceId = ObjectId(userDeviceId),
    role = role
)
