package com.sushkevych.securitydevices.device.application.port

import com.sushkevych.securitydevices.core.application.port.CoreRepository
import com.sushkevych.securitydevices.device.domain.Device

interface DeviceRepositoryOutPort : CoreRepository<Device, String>
