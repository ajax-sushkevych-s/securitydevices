package com.sushkevych.securitydevices.dto.response

import com.sushkevych.securitydevices.model.MongoDevice
import jakarta.validation.constraints.NotEmpty

data class DeviceDtoResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<MongoDevice.MongoDeviceAttribute>?
)

fun MongoDevice.toResponse() = DeviceDtoResponse(
    id = id?.toHexString(),
    name = name,
    description = description,
    type = type,
    attributes = attributes
)
