package com.sushkevych.securitydevices.device.infrastructure.mapper

import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.device.infrastructure.dto.request.DeviceAttributeRequest
import com.sushkevych.securitydevices.device.infrastructure.dto.request.DeviceRequest

fun DeviceRequest.toDevice(): Device {
    return Device(
        id = id,
        name = name,
        description = description,
        type = type,
        attributes = attributes.map { it.toDeviceAttribute() }
    )
}

fun DeviceAttributeRequest.toDeviceAttribute(): Device.DeviceAttribute {
    return Device.DeviceAttribute(
        attributeType = this.attributeType,
        attributeValue = this.attributeValue
    )
}
