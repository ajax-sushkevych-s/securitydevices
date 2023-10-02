package com.sushkevych.securitydevices.dto.response

import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.commonmodels.device.DeviceAttribute
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

fun DeviceResponse.toDevice(): Device {
    return Device.newBuilder()
        .setId(this.id)
        .setName(this.name)
        .setDescription(this.description)
        .setType(this.type)
        .addAllAttributes(this.attributes.map { it?.toDeviceAttribute() })
        .build()
}

fun DeviceAttributeResponse.toDeviceAttribute(): DeviceAttribute {
    return DeviceAttribute.newBuilder()
        .setAttributeType(this.attributeType)
        .setAttributeValue(this.attributeValue)
        .build()
}

fun Device.toDeviceResponse(): DeviceResponse {
    return DeviceResponse(
        id = id,
        name = name,
        description = description,
        type = type,
        attributes = attributesList.map { it.toDeviceAttributeResponse() }
    )
}

fun DeviceAttribute.toDeviceAttributeResponse(): DeviceAttributeResponse {
    return DeviceAttributeResponse(
        attributeType = attributeType,
        attributeValue = attributeValue
    )
}
