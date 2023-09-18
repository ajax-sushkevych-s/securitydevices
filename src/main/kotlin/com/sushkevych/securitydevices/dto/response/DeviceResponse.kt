package com.sushkevych.securitydevices.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.sushkevych.securitydevices.model.MongoDevice

data class DeviceResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<DeviceAttributeResponse?>
)

data class DeviceAttributeResponse(
    @field:JsonProperty(value = "attribute_type")
    val attributeType: String?,
    @field:JsonProperty(value = "attribute_value")
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
