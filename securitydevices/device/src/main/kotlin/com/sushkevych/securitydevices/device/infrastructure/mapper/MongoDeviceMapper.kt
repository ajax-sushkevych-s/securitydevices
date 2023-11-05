package com.sushkevych.securitydevices.device.infrastructure.mapper

import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.device.infrastructure.repository.entity.MongoDevice

fun MongoDevice.toDevice(): Device {
    return Device(
        id = id,
        name = name,
        description = description,
        type = type,
        attributes = attributes.map { it?.toDeviceAttribute() }
    )
}

fun MongoDevice.MongoDeviceAttribute?.toDeviceAttribute(): Device.DeviceAttribute? {
    return this?.let {
        Device.DeviceAttribute(
            attributeType = it.attributeType,
            attributeValue = it.attributeValue
        )
    }
}
