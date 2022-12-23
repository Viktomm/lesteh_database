package com.mgul.dbrobo.services;

import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public void save(Device device) {
        deviceRepository.insert(device);
    }
}
