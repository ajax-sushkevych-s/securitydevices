package com.sushkevych.internalapi

object NatsSubject {
    private const val REQUEST_PREFIX = "com.sushkevych.securitydevices.input.request"

    private const val EVENT_PREFIX = "com.sushkevych.securitydevices.output.pubsub"

    object DeviceRequest {
        private const val DEVICE_PREFIX = "$REQUEST_PREFIX.device"

        const val CREATE = "$DEVICE_PREFIX.create"
        const val GET_BY_ID = "$DEVICE_PREFIX.get_by_id"
        const val GET_ALL = "$DEVICE_PREFIX.get_all"
        const val UPDATE = "$DEVICE_PREFIX.update"
        const val DELETE = "$DEVICE_PREFIX.delete"
    }

    object DeviceEvent {
        private const val DEVICE_PREFIX = "$EVENT_PREFIX.device"

        const val UPDATED = "$DEVICE_PREFIX.updated"
    }
}
