package com.sushkevych.securitydevices.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.sushkevych.securitydevices.model.User
import jakarta.validation.constraints.NotEmpty

data class UserDtoRequest(
    val id: Long?,
    @field:NotEmpty(message = "Username cannot be empty.")
    val username: String,
    @field:NotEmpty(message = "Email cannot be empty.")
    val email: String,
    @field:NotEmpty(message = "Mobile number cannot be empty.")
    @field:JsonProperty(value = "mobile_number")
    val mobileNumber: String,
    val password: String
)

fun UserDtoRequest.toEntity() = User(
    id = id,
    username = username,
    email = email,
    mobileNumber = mobileNumber,
    password = password
)
