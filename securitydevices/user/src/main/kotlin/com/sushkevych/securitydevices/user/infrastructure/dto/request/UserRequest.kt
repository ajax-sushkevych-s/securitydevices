package com.sushkevych.securitydevices.user.infrastructure.dto.request

import jakarta.validation.constraints.NotEmpty

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
    val role: UserRoleRequest?
)

enum class UserRoleRequest {
    OWNER,
    VIEWER
}
