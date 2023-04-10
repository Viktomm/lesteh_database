package com.mgul.dbrobo.services;

import com.mgul.dbrobo.exceptions.DeviceNotFoundException;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.models.Place;
import com.mgul.dbrobo.repositories.DeviceRepository;
import com.mgul.dbrobo.repositories.EntryRepository;
import com.mgul.dbrobo.repositories.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final PlaceRepository placeRepository;

    private final EntryRepository entryRepository;
    private final int pageSize=3;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, PlaceRepository placeRepository, EntryRepository entryRepository) {
        this.deviceRepository = deviceRepository;
        this.placeRepository = placeRepository;
        this.entryRepository = entryRepository;
    }

    public List<String> findAllDeviceName() { return deviceRepository.findAll().stream().map(x -> x.getName()).toList();}

    public List<Device> findAll() { return deviceRepository.findAll();}
    @Transactional
    public void save(Device device) {
        if (isPlaceCorrect(device.getObject())) {
            deviceRepository.insert(device);
        } else {
            throw new DeviceNotFoundException("No such object in database");
        }
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
    public void delete(Long id) {
        Optional<Device> foundDevice = deviceRepository.findById(id);
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
    public Device findById(Long id) {
        return deviceRepository.findById(id).get();
    }
    public int getCount(String name, String serial, String x, String y, String object, String removed) {
        return (int)Math.ceil(getDeviceList(name, serial, x, y, object, removed).size()/((double)pageSize));
    }

    public List<Device> findByParams(String name, String serial, String x, String y, String object, String removed,int page) {
        List<Device> ans = getDeviceList(name, serial, x, y, object, removed);
        int startOfPage = (page-1)*pageSize;
        int endOfPage = (startOfPage+pageSize >= ans.size())? ans.size() : startOfPage+pageSize;
        return (ans.size()==0)? ans : ans.subList(startOfPage,endOfPage);
    }

    private List<Device> getDeviceList(String name, String serial, String x, String y, String object, String removed) {
        List<Device> all = deviceRepository.findAll();
        List<Device> ans = new ArrayList<>(all);
        for(Device device:all) {
            if (!device.getName().toUpperCase().contains(name.toUpperCase())) {
                ans.remove(device);
                continue;
            }
            if (!device.getSerial().toUpperCase().contains(serial.toUpperCase())) {
                ans.remove(device);
                continue;
            }
            if (!x.isEmpty() && device.getX()!=Double.parseDouble(x)) {
                ans.remove(device);
                continue;
            }
            if (!y.isEmpty() && device.getX()!=Double.parseDouble(x)) {
                ans.remove(device);
                continue;
            }
            if (!device.getObject().toUpperCase().contains(object.toUpperCase())) {
                ans.remove(device);
                continue;
            }
            if (!removed.isEmpty() && device.getRemoved()!=Integer.parseInt(removed)) {
                ans.remove(device);
            }
        }
        ans.sort(Comparator.comparing(Device::getId));
        return ans;
    }
}
