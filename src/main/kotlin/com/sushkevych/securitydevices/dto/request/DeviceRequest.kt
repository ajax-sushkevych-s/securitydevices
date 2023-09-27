package com.sushkevych.securitydevices.dto.request

import com.sushkevych.securitydevices.model.MongoDevice
import jakarta.validation.constraints.NotEmpty
import org.bson.types.ObjectId

data class DeviceRequest(
    val id: String?,
    @field:NotEmpty(message = "Name cannot be empty.")
    val name: String,
    @field:NotEmpty(message = "Description cannot be empty.")
    val description: String,
    @field:NotEmpty(message = "Type cannot be empty.")
    val type: String,
    val attributes: List<DeviceAttributeRequest>
)

data class DeviceAttributeRequest(
    val attributeType: String?,
    val attributeValue: String?
)

fun DeviceRequest.toEntity() = MongoDevice(
    id = id?.let { ObjectId(id) },
    name = name,
    description = description,
    type = type,
    attributes = attributes.map { it.toEntity() }
)

fun DeviceAttributeRequest.toEntity() = MongoDevice.MongoDeviceAttribute(
    attributeType = attributeType,
    attributeValue = attributeValue
)
