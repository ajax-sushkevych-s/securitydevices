package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.model.Device
import com.sushkevych.securitydevices.repository.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeviceService @Autowired constructor(private val deviceRepository: DeviceRepository) {
    fun getDeviceById(deviceId: Long): Device {
        return deviceRepository.getReferenceById(deviceId)
    }

    fun getAllDevices(): List<Device> {
        return deviceRepository.findAll()
    }

    fun saveDevice(device: Device): Device {
        return deviceRepository.save(device)
    }

    fun deleteDevice(deviceId: Long) {
        deviceRepository.deleteById(deviceId)
    }
}
