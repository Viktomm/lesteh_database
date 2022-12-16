package com.mgul.dbrobo.services;

import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.repositories.DeviceRepository;
import com.mgul.dbrobo.repositories.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EntryService {
    private final EntryRepository entryRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    public EntryService(EntryRepository entryRepository, DeviceRepository deviceRepository) {
        this.entryRepository = entryRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<Entry> findAll(){
        return entryRepository.findAll();
    }

    public void insertOne(Map<String, Map<String,String>> entryData){
        Optional<Device> device = deviceRepository.findByAkey(entryData.get("system").get("Akey"));
        if (device.isPresent()) {
            String deviceName = device.get().getName();
            entryData.remove("system");
            Entry entry = new Entry();
            //TODO:боже пофиксите кто-нибудь эту дату, я не  умею((
            if (entryData.containsKey("RTC")) {
                try {
                    String[] stringOfDates = (entryData.get("RTC").get("date")+":"+entryData.get("RTC").get("time")).split("[:-]");
                    Integer[] dates = new Integer[6];
                    for (int i = 0; i < 6; i++) {
                        dates[i]=Integer.valueOf(stringOfDates[i]);
                    }
                    entry.setCreatedAt(LocalDateTime.of(dates[0],dates[1],dates[2],dates[3],dates[4],dates[5]));
                } catch (Exception e) {
                    entry.setCreatedAt(LocalDateTime.now());
                }
            } else {
                entry.setCreatedAt(LocalDateTime.now());
            }
            entry.setDeviceName(deviceName);
            entry.setData(entryData);
            entryRepository.insert(entry);
        }
        else
            throw new WrongAKeyException("There's no device with such aKey");

    }

    //TODO:Тут запрос клоунский, его бы переписать через SQL
    //я его конечно переделал, но кажись он всё ещё клоунский :(
    public List<Entry> firstTenEntries() {
        return entryRepository.findAll(PageRequest.of(0,2,Sort.by(Sort.Direction.DESC,"createdAt"))).toList();
        //return entryRepository.findAll(Sort.by(Sort.Order.desc("createdAt"))).subList(0,2);
    }
}
