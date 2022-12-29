package com.mgul.dbrobo.services;

import com.mgul.dbrobo.exceptions.DeviceNotFoundException;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.models.Place;
import com.mgul.dbrobo.repositories.DeviceRepository;
import com.mgul.dbrobo.repositories.EntryRepository;
import com.mgul.dbrobo.repositories.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final PlaceRepository placeRepository;

    private final EntryRepository entryRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, PlaceRepository placeRepository, EntryRepository entryRepository) {
        this.deviceRepository = deviceRepository;
        this.placeRepository = placeRepository;
        this.entryRepository = entryRepository;
    }

    @Transactional
    public void save(Device device) {
        if (isPlaceCorrect(device.getObject())) {
            deviceRepository.insert(device);
        } else {
            throw new DeviceNotFoundException("No such object in database");
        }
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
        if (device.getObject()!=null) {
            if (isPlaceCorrect(device.getObject()))
                d.setObject(device.getObject());
        }
        d.setRemoved(device.getRemoved());
        deviceRepository.save(d);
    }

    private boolean isPlaceCorrect(String name) {
        List<Place> places = placeRepository.findAll();
        boolean isPlaceExist=false;
        for(Place place:places) {
            if (place.getName().equals(name)) {
                isPlaceExist=true;
                break;
            }
        }
        return isPlaceExist;
    }

    public void delete(Device device) {
        Optional<Device> foundDevice = deviceRepository.findById(device.getId());
        if (foundDevice.isEmpty()) throw new DeviceNotFoundException("No device with such id");
        Device d = foundDevice.get();
        d.setRemoved(1);
        deviceRepository.save(d);
    }

    public List<String> getSensorsList(Long id) {
        Device device = deviceRepository.findById(id).get();
        Entry entry = entryRepository.findFirstByuNameAndSerial(device.getName(), device.getSerial());
        Set<String> sensors = new TreeSet<>(entry.getData().keySet());
        sensors.removeIf(x-> x.startsWith("system"));
        sensors.removeIf(x-> x.startsWith("RTC"));
        return sensors.stream().toList();
    }
}
