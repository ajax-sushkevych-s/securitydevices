package com.sushkevych.securitydevices.dto.request

import com.sushkevych.securitydevices.model.Device
import jakarta.validation.constraints.NotEmpty

data class DeviceDtoRequest(
    val id: Long?,
    @field:NotEmpty(message = "Name cannot be empty.")
    val name: String,
    @field:NotEmpty(message = "Description cannot be empty.")
    val description: String,
    @field:NotEmpty(message = "Type cannot be empty.")
    val type: String
)

fun DeviceDtoRequest.toEntity() = Device(
    id = id,
    name = name,
    description = description,
    type = type
)
