package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.services.DeviceService;
import com.mgul.dbrobo.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/core")
public class DataController {
    private final EntryService entryService;

    private final DeviceService deviceService;


    @Autowired
    public DataController(EntryService entryService, DeviceService deviceService) {
        this.entryService = entryService;
        this.deviceService = deviceService;
    }

    @GetMapping()
    public List<Entry> findAll() {
        return entryService.findAll();
    }


    @PostMapping("/jsonapp.php")
    public void insertOne(@RequestBody LinkedHashMap<String, LinkedHashMap<String, String>> allData) {
        entryService.insertOne(allData);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(WrongAKeyException e) {
        // в HTTP ответе будет тело (String) и статус в заголовке
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @GetMapping("/devices")
    @ResponseBody
    public Map<Long, String> getAllDevicesInMap() {
        return deviceService.findAllWithIdAndName();
    }

    @GetMapping(value = "/deb.php", params = {"fileback"})
    public Map<String, Entry> loadDataBetweenTextJSON
            (@RequestParam("fdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fdate,
             @RequestParam("sdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sdate) {
        return entryService.getDataBetween(fdate.atZone(ZoneId.systemDefault()).toLocalDateTime(),
                sdate.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @GetMapping(value = "/deb.php", params = {"manualmode"})
    public String loadDataBetweenCSV(@RequestParam("fdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                 LocalDateTime fdate,
                                             @RequestParam("sdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                 LocalDateTime sdate,
                                             @RequestParam("unitid") Long deviceId) {
        String csv = entryService.getDataBetweenCSV(fdate, sdate, deviceId);
        return csv;
    }
}
