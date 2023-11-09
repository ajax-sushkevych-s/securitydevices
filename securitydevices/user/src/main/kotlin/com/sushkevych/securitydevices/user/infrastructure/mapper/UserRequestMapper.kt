package com.sushkevych.securitydevices.user.infrastructure.mapper

import com.sushkevych.securitydevices.user.domain.User
import com.sushkevych.securitydevices.user.infrastructure.dto.request.UserDeviceRequest
import com.sushkevych.securitydevices.user.infrastructure.dto.request.UserRequest
import com.sushkevych.securitydevices.user.infrastructure.dto.request.UserRoleRequest

fun UserRequest.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        mobileNumber = this.mobileNumber,
        password = this.password,
        devices = this.devices.map { it.toUserDevice() }
    )
}

fun UserDeviceRequest.toUserDevice(): User.UserDevice {
    return User.UserDevice(
        deviceId = this.deviceId,
        userDeviceId = this.userDeviceId,
        role = this.role?.toUserRole()
    )
}

fun UserRoleRequest.toUserRole(): User.UserRole {
    return when (this) {
        UserRoleRequest.OWNER -> User.UserRole.OWNER
        UserRoleRequest.VIEWER -> User.UserRole.VIEWER
    }
}
