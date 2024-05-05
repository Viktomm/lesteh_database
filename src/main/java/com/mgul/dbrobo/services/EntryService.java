package com.mgul.dbrobo.services;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import com.mgul.dbrobo.exceptions.EntryNotFoundException;
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

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

    public void insertOne(LinkedHashMap<String, LinkedHashMap<String,JsonNode>> entryData){
        entryRepository.insert(transformation(entryData));
    }

    public void appendValueToField(JsonNode node, String field) {

    }

    private Entry transformation(LinkedHashMap<String, LinkedHashMap<String, JsonNode>> entryData) {
        Entry entry = new Entry();
        LinkedHashMap<String,String> newEntryData = new LinkedHashMap<>();
        Optional<Device> device = deviceRepository.findByAkey(entryData.get("system").get("Akey").asText());
        if (device.isPresent()) {
            entryData.get("system").remove("Akey");
            String deviceName = device.get().getName();
            String deviceSerial = device.get().getSerial();
            for (String key : entryData.keySet()) {
                LinkedHashMap<String,JsonNode> value = entryData.get(key);
                for (String innerKey : value.keySet()) {
                    newEntryData.put(key + "_" + innerKey, value.get(innerKey).asText());
                }
            }
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //TODO:боже пофиксите кто-нибудь эту дату, я не  умею((
            if (entryData.containsKey("RTC")) {
                try {
                    String[] stringOfDates = (entryData.get("RTC").get("date") + ":" + entryData.get("RTC").get("time")).split("[:-]");
                    Integer[] dates = new Integer[6];
                    for (int i = 0; i < 6; i++) {
                        dates[i]=Integer.valueOf(stringOfDates[i]);
                    }
                    entry.setDateForCalculation(LocalDateTime.of(dates[0],dates[1],dates[2],dates[3],dates[4],dates[5]));
                    entry.setDate(format.format(Timestamp.valueOf(LocalDateTime.of(dates[0],dates[1],dates[2],dates[3],dates[4],dates[5]))));
                } catch (Exception e) {
                    entry.setDateForCalculation(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
                    entry.setDate(format.format(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Europe/Moscow")))));
                }
            } else {
                entry.setDateForCalculation(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
                entry.setDate(format.format(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Europe/Moscow")))));
            }
            entry.setUName(deviceName);
            entry.setSerial(deviceSerial);
            entry.setData(newEntryData);
            return entry;
        }
        else
            throw new WrongAKeyException("There's no device with such aKey");
    }

    public void insertMany(List<LinkedHashMap<String, LinkedHashMap<String, JsonNode>>> allData){
        List<Entry> entries = new ArrayList<>();
        for(LinkedHashMap<String, LinkedHashMap<String, JsonNode>> singleData:allData) {
            entries.add(transformation(singleData));
        }
        entryRepository.insert(entries);
    }


    public List<Entry> lastTenEntries(String uName, String serial, Boolean whichDate) {
        String date = whichDate ? "Date" : "dateForCalculation";
        if (uName.isEmpty() && serial.isEmpty()) {
            return entryRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, date))).toList();
        }
        return entryRepository.findAllByuNameContainingIgnoreCaseAndSerialContainingIgnoreCase(uName, serial, PageRequest.of(0,10,Sort.by(Sort.Direction.DESC, date))).toList();
    }

    public String getDataBetweenCSV(LocalDateTime fdate, LocalDateTime sdate, Long deviceId) {

        Device device = deviceRepository.findById(deviceId).get();
        List<Entry> result = entryRepository.findByuNameAndSerialAndDateForCalculationBetween(device.getName(), device.getSerial(), fdate, sdate);

        if (result.stream().findAny().isEmpty()) throw new EntryNotFoundException(fdate, sdate,
                deviceId, deviceRepository.findAll(),
                "Записей по прибору " + device.getName() + " (" + device.getSerial() + ") в период с "
                        + fdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + " до " + sdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " не найдено");
        // FIXME: 15.05.2023 Сделать баля по-человечески баля
        // FIXME: 31.12.2023 Ничего баля не поменялось баля
        // FIXME: 14.04.2024 А в принципе хорошо баля
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setColumnSeparator(';').setLineSeparator('\n');
        ObjectMapper mapper = new ObjectMapper();

        Entry entryFirst = result.stream().findAny().get();
        JsonNode jsonTree = mapper.valueToTree(entryFirst);
        JsonNode jsonNodeData = jsonTree.get("data");
        csvSchemaBuilder.addColumn("date");
        jsonNodeData.fieldNames().forEachRemaining(field -> csvSchemaBuilder.addColumn(field));
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        CharArrayWriter writer = new CharArrayWriter();
        try {
            writer.write("\uFEFF");
            String headers = csvMapper
                    .writerFor(JsonNode.class)
                    .with(csvSchema)
                    .writeValueAsString(null);

            writer.write(String.format("Прибор: ;%s;Интервал: ;%s; / ;%s;\n", device.getName() + " (" + device.getSerial() + ")",
                    fdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    sdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write(headers);
        } catch (IOException ex) {throw new RuntimeException();}


        for (Entry entry : result) {
            jsonTree = mapper.valueToTree(entry);
            jsonNodeData = jsonTree.get("data");
            String str1;
            String str2;
            try {
                str1 = csvMapper
                        .configure(JsonGenerator.Feature.IGNORE_UNKNOWN,true)
                        .writerFor(JsonNode.class)
                        .with(csvSchema.withoutHeader())
                        .writeValueAsString(jsonTree);
                str2 = csvMapper
                        .writerFor(JsonNode.class)
                        .with(csvSchema.withoutHeader())
                        .writeValueAsString(jsonNodeData);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                writer.write(str1.replaceAll(";", "").replaceAll("\n", "")
                        + str2.replaceAll("\\.", ","));
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        }
        return writer.toString();
    }

    public Map<String,Entry> getDataBetween(LocalDateTime fdate, LocalDateTime sdate) {
        List<Entry> fromDb = entryRepository.findByDateForCalculationBetween(fdate,sdate);
        LinkedHashMap<String,Entry> result = new LinkedHashMap<>();
        for(Entry entry : fromDb) {
            try {
                Method getId = entry.getClass().getDeclaredMethod("getIdNotForSpring");
                getId.setAccessible(true);
                result.put((String)getId.invoke(entry),entry);
            } catch (Exception e) {
                throw new RuntimeException("Reflection error");
            }
        }
        return result;
    }

    public void saveDataFromFile(MultipartFile file) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream in = file.getInputStream();
            ArrayList<LinkedHashMap<String, LinkedHashMap<String, JsonNode>>> payload;
            List<LinkedHashMap<String, LinkedHashMap<String, String>>> test = new ArrayList<>();
            payload = objectMapper.readValue(in, new TypeReference<ArrayList<LinkedHashMap<String, LinkedHashMap<String, JsonNode>>>>(){});
            insertMany(payload);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
