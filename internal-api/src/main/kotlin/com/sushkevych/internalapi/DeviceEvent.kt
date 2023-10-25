package com.sushkevych.internalapi

import com.sushkevych.internalapi.Constants.EVENT_PREFIX

object DeviceEvent {
    private const val DEVICE_PREFIX = "${EVENT_PREFIX}.device"

    const val UPDATED = "updated"

    fun createDeviceEventSubject(deviceId: String, eventType: String): String =
        "$DEVICE_PREFIX.$deviceId.$eventType"
}
