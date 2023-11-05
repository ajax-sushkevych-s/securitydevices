package com.sushkevych.securitydevices.user.domain

data class User(
    val id: String?,
    val username: String,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val devices: List<UserDevice>
) {
    data class UserDevice(
        val deviceId: String?,
        val userDeviceId: String?,
        val role: UserRole?
    )

    enum class UserRole {
        OWNER,
        VIEWER
    }
}


