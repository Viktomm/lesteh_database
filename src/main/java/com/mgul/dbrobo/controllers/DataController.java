package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.services.DeviceService;
import com.mgul.dbrobo.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
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
    public ResponseEntity findAll() {
        return ResponseEntity.ok(entryService.findAll());
    }


    @PostMapping("/jsonapp.php")
    public ResponseEntity insertOne(@RequestBody LinkedHashMap<String, LinkedHashMap<String, String>> allData) {
        try {
            entryService.insertOne(allData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (WrongAKeyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(value = "/deb.php/text") //
    @ResponseBody
    public Map<String, Entry> loadDataBetweenTextJSON
            (@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fdate,
             @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime sdate) {
        return entryService.getDataBetween(fdate.atZone(ZoneId.systemDefault()).toLocalDateTime(),
                sdate.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @GetMapping( "/deb.php")
    public String getDataBetween(@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime fdate,
                                 @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                 LocalDateTime sdate,
                                 @RequestParam("jsonOrCsv") Boolean jsonOrCsv,
                                       Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("fdate", dtf.format(fdate));
        model.addAttribute("sdate", dtf.format(sdate));
        if (jsonOrCsv) {
            return "apifilebackJSON";
        } else {
            model.addAttribute("fdate", fdate);
            model.addAttribute("sdate", sdate);
            model.addAttribute("devices", deviceService.findAll());
            return "apifilebackCSV";
        }
    }

    @GetMapping(value = "/deb.php/log.csv")
    public ResponseEntity loadDataBetweenCSV(@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                 LocalDateTime fdate,
                                             @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                 LocalDateTime sdate,
                                             @RequestParam("deviceName") String deviceName) {
        File file = entryService.getDataBetweenCSV(fdate, sdate, deviceName);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=log.csv")
                .contentLength(file.length())
                .header("Content-Type", "text/csv; charset=utf-8")
                .body(new FileSystemResource(file));
    }

}
