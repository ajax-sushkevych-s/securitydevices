package com.sushkevych.securitydevices.device.infrastructure.mapper

import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent

fun Device.mapToDeviceUpdatedEvent(): DeviceUpdatedEvent =
    DeviceUpdatedEvent.newBuilder()
        .setDevice(this)
        .build()
