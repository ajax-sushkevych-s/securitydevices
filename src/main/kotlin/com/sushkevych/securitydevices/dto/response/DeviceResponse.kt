package com.sushkevych.securitydevices.dto.response

import com.sushkevych.securitydevices.model.MongoDevice

data class DeviceResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<DeviceAttributeResponse?>
)

data class DeviceAttributeResponse(
    val attributeType: String?,
    val attributeValue: String?
)

fun MongoDevice.toResponse() = DeviceResponse(
    id = id?.toHexString(),
    name = name,
    description = description,
    type = type,
    attributes = attributes.map { it?.toResponse() }
)

fun MongoDevice.MongoDeviceAttribute.toResponse() = DeviceAttributeResponse(
    attributeType = attributeType,
    attributeValue = attributeValue
)
