package com.sushkevych.securitydevices.mappers

import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent

fun Device.mapToDeviceUpdatedEvent(): DeviceUpdatedEvent =
    DeviceUpdatedEvent.newBuilder()
        .setDevice(this)
        .build()
