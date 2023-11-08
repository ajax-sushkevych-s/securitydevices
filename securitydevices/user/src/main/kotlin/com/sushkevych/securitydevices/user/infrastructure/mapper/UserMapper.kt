package com.sushkevych.securitydevices.user.infrastructure.mapper

import com.sushkevych.securitydevices.user.domain.User
import com.sushkevych.securitydevices.user.infrastructure.dto.response.UserDeviceResponse
import com.sushkevych.securitydevices.user.infrastructure.dto.response.UserResponse
import com.sushkevych.securitydevices.user.infrastructure.dto.response.UserRoleResponse
import com.sushkevych.securitydevices.user.infrastructure.adapters.repository.entity.MongoUser
import org.bson.types.ObjectId

fun User.toMongoUser(): MongoUser {
    val deviceList = devices.map { device ->
        MongoUser.MongoUserDevice(
            deviceId = ObjectId(device.deviceId),
            userDeviceId = ObjectId(device.userDeviceId),
            role = device.role?.let { mapUserRole(it) }
        )
    }
    return MongoUser(
        id = id,
        username = username,
        email = email,
        mobileNumber = mobileNumber,
        password = password,
        devices = deviceList
    )
}

private fun mapUserRole(userRole: User.UserRole): MongoUser.MongoUserRole {
    return when (userRole) {
        User.UserRole.OWNER -> MongoUser.MongoUserRole.OWNER
        User.UserRole.VIEWER -> MongoUser.MongoUserRole.VIEWER
    }
}

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username,
        email = this.email,
        mobileNumber = this.mobileNumber,
        devices = this.devices.map { it.toUserDeviceResponse() }
    )
}

fun User.UserDevice.toUserDeviceResponse(): UserDeviceResponse {
    return UserDeviceResponse(
        deviceId = this.deviceId,
        userDeviceId = this.userDeviceId,
        role = this.role?.toUserRoleResponse()
    )
}

fun User.UserRole.toUserRoleResponse(): UserRoleResponse {
    return when (this) {
        User.UserRole.OWNER -> UserRoleResponse.OWNER
        User.UserRole.VIEWER -> UserRoleResponse.VIEWER
    }
}
