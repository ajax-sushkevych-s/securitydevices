package com.sushkevych.securitydevices.dto.response

import com.sushkevych.securitydevices.model.Device

data class DeviceDtoResponse(
    val id: Long?,
    val name: String,
    val description: String,
    val type: String
)

fun Device.toResponse() = DeviceDtoResponse(
    id = id,
    name = name,
    description = description,
    type = type
)
