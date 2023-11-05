package com.sushkevych.securitydevices.user.infrastructure.mapper

import com.sushkevych.securitydevices.user.domain.User
import com.sushkevych.securitydevices.user.infrastructure.repository.entity.MongoUser

fun MongoUser.toUser(): User {
    val deviceList = devices.map { device ->
        User.UserDevice(
            deviceId = device?.deviceId?.toString(),
            userDeviceId = device?.userDeviceId?.toString(),
            role = device?.role?.let { mapUserRole(it) }
        )
    }
    return User(
        id = id,
        username = username.orEmpty(),
        email = email.orEmpty(),
        mobileNumber = mobileNumber.orEmpty(),
        password = password.orEmpty(),
        devices = deviceList
    )
}

private fun mapUserRole(mongoUserRole: MongoUser.MongoUserRole): User.UserRole? {
    return when (mongoUserRole) {
        MongoUser.MongoUserRole.OWNER -> User.UserRole.OWNER
        MongoUser.MongoUserRole.VIEWER -> User.UserRole.VIEWER
    }
}
