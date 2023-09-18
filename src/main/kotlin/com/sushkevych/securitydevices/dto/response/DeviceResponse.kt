package com.sushkevych.securitydevices.dto.response

import com.sushkevych.securitydevices.model.MongoDevice

data class DeviceResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<MongoDevice.MongoDeviceAttribute?>
)

fun MongoDevice.toResponse() = DeviceResponse(
    id = id?.toHexString(),
    name = name,
    description = description,
    type = type,
    attributes = attributes
)
