package com.sushkevych.securitydevices.device.application.port

import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.core.application.port.CoreService

interface DeviceService : CoreService<Device, String>
