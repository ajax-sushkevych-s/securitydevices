package com.sushkevych.securitydevices.dto.request

import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.commonmodels.device.DeviceAttribute
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

fun Device.toDeviceRequest(): DeviceRequest {
    return DeviceRequest(
        id = this.id,
        name = this.name,
        description = this.description,
        type = this.type,
        attributes = this.attributesList.map { it.toDeviceAttributeRequest() }
    )
}

fun DeviceAttribute.toDeviceAttributeRequest(): DeviceAttributeRequest {
    return DeviceAttributeRequest(
        attributeType = this.attributeType,
        attributeValue = this.attributeValue
    )
}
