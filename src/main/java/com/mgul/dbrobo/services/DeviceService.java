package com.mgul.dbrobo.services;

import com.mgul.dbrobo.exceptions.DeviceNotFoundException;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.repositories.DeviceRepository;
import com.mgul.dbrobo.repositories.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final PlaceRepository placeRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, PlaceRepository placeRepository) {
        this.deviceRepository = deviceRepository;
        this.placeRepository = placeRepository;
    }

    @Transactional
    public void save(Device device) {

        deviceRepository.insert(device);
    }

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public void update(Device device) {
        Optional<Device> foundDevice = deviceRepository.findById(device.getId());
        if (foundDevice.isEmpty()) throw new DeviceNotFoundException("No device with such id");
        Device d = foundDevice.get();
        if (!device.getName().isBlank()) d.setName(device.getName());
        if (!device.getSerial().isBlank()) d.setSerial(device.getSerial());
        if (!device.getAkey().isBlank()) d.setAkey(device.getAkey());
        if (device.getX()!=0) d.setX(device.getX());
        if (device.getY()!=0) d.setY(device.getY());
        if (device.getObjId()!=0) {
            d.setObjId(device.getObjId());
            d.setObject(placeRepository.findById(device.getObjId()).get().getName());
        }
        d.setRemoved(device.getRemoved());
        deviceRepository.save(d);
    }

    public void delete(Device device) {
        Optional<Device> foundDevice = deviceRepository.findById(device.getId());
        if (foundDevice.isEmpty()) throw new DeviceNotFoundException("No device with such id");
        Device d = foundDevice.get();
        d.setRemoved(1);
        deviceRepository.save(d);
    }
}
