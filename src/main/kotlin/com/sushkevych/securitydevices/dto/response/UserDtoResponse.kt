package com.sushkevych.securitydevices.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.sushkevych.securitydevices.model.User

data class UserDtoResponse(
    val id: Long?,
    val username: String,
    val email: String,
    @field:JsonProperty(value = "mobile_number")
    val mobileNumber: String
)

fun User.toResponse() = UserDtoResponse(
    id = id,
    username = username,
    email = email,
    mobileNumber = mobileNumber
)

