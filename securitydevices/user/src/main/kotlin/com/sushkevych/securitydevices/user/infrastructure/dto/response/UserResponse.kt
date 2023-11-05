package com.sushkevych.securitydevices.user.infrastructure.dto.response

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
    val role: UserRoleResponse?
)

enum class UserRoleResponse {
    OWNER,
    VIEWER
}
