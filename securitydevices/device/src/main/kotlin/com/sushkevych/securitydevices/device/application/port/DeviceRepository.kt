package com.sushkevych.securitydevices.device.application.port

import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.core.application.port.CoreRepository

interface DeviceRepository : CoreRepository<Device, String>
