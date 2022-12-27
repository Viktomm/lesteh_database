package com.mgul.dbrobo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.repositories.DeviceRepository;
import com.mgul.dbrobo.repositories.EntryRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

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

    public void insertOne(LinkedHashMap<String, LinkedHashMap<String,String>> entryData){
        Entry entry = new Entry();
        LinkedHashMap<String,String> newEntryData = new LinkedHashMap<>();
        Optional<Device> device = deviceRepository.findByAkey(entryData.get("system").get("Akey"));
        if (device.isPresent()) {
            entryData.get("system").remove("Akey");
            String deviceName = device.get().getName();
            String deviceSerial = device.get().getSerial();
            for(String key: entryData.keySet()){
                Map<String,String> value = entryData.get(key);
                for(String innerKey: value.keySet()) {
                    newEntryData.put(key+"_"+innerKey,value.get(innerKey));
                }
            }
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //TODO:боже пофиксите кто-нибудь эту дату, я не  умею((
            if (entryData.containsKey("RTC")) {
                try {
                    String[] stringOfDates = (entryData.get("RTC").get("date")+":"+entryData.get("RTC").get("time")).split("[:-]");
                    Integer[] dates = new Integer[6];
                    for (int i = 0; i < 6; i++) {
                        dates[i]=Integer.valueOf(stringOfDates[i]);
                    }
                    entry.setDateForCalculation(LocalDateTime.of(dates[0],dates[1],dates[2],dates[3],dates[4],dates[5]));
                    entry.setDate(format.format(Timestamp.valueOf(LocalDateTime.of(dates[0],dates[1],dates[2],dates[3],dates[4],dates[5]))));
                } catch (Exception e) {
                    entry.setDateForCalculation(LocalDateTime.now());
                    entry.setDate(format.format(Timestamp.valueOf(LocalDateTime.now())));
                }
            } else {
                entry.setDateForCalculation(LocalDateTime.now());
                entry.setDate(format.format(Timestamp.valueOf(LocalDateTime.now())));
            }
            entry.setuName(deviceName);
            entry.setSerial(deviceSerial);
            entry.setData(newEntryData);
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



    public File getDataBetweenCSV(LocalDateTime fdate, LocalDateTime sdate) {
        LinkedHashMap<String,Entry> result = (LinkedHashMap<String, Entry>) getDataBetween(fdate, sdate);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonTree = mapper.valueToTree(result);

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(fieldName -> {
            csvSchemaBuilder.addColumn(fieldName);
        });
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        try {
            CsvMapper csvMapper = new CsvMapper();
            csvMapper.writerFor(JsonNode.class)
                    .with(csvSchema)
                    .writeValue(new File("src/main/resources/log.csv"), jsonTree);
        } catch (IOException ex) {
            throw new RuntimeException();
        }
        return new File("src/main/resources/log.csv");
    }

    public Map<String,Entry> getDataBetween(LocalDateTime fdate, LocalDateTime sdate) {
        List<Entry> fromDb = entryRepository.findByDateForCalculationBetween(fdate,sdate);
        LinkedHashMap<String,Entry> result = new LinkedHashMap<>();
        for(Entry entry:fromDb) {
            try {
                Method getId = entry.getClass().getDeclaredMethod("getIdNotForSpring");
                getId.setAccessible(true);
                result.put((String)getId.invoke(entry),entry);
            } catch (Exception e) {
                throw new RuntimeException("Кто-то сломал рефлексию =(");
            }
        }
        return result;
    }
}
