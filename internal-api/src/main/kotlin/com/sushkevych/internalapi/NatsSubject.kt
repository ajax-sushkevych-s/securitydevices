package com.sushkevych.internalapi

import com.sushkevych.internalapi.Constants.REQUEST_PREFIX

object NatsSubject {
    object DeviceRequest {
        private const val DEVICE_PREFIX = "$REQUEST_PREFIX.device"

        const val CREATE = "$DEVICE_PREFIX.create"
        const val GET_BY_ID = "$DEVICE_PREFIX.get_by_id"
        const val GET_ALL = "$DEVICE_PREFIX.get_all"
        const val UPDATE = "$DEVICE_PREFIX.update"
        const val DELETE = "$DEVICE_PREFIX.delete"
    }
}
