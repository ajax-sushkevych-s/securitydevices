package com.sushkevych.internalapi

import com.sushkevych.internalapi.MessageDestinations.EVENT_PREFIX

object DeviceEvent {
    private const val DEVICE_PREFIX = "$EVENT_PREFIX.device"

    const val UPDATED = "updated"

    fun createDeviceEventNatsSubject(deviceId: String, eventType: String): String =
        "$DEVICE_PREFIX.$deviceId.$eventType"

    fun createDeviceEventKafkaTopic(eventType: String): String = "$DEVICE_PREFIX.$eventType"
}
