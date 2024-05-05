package com.mgul.dbrobo.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mgul.dbrobo.exceptions.EntryNotFoundException;
import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.models.IntervalDataDTO;
import com.mgul.dbrobo.services.DeviceService;
import com.mgul.dbrobo.services.EntryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
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
    public ResponseEntity insertOne(@RequestBody LinkedHashMap<String, LinkedHashMap<String, JsonNode>> allData) {
        try {
            entryService.insertOne(allData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (WrongAKeyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping( "/deb.php")
    public String getDataBetween(@ModelAttribute("intervalData") @Valid IntervalDataDTO intervalDataDTO,
                                 BindingResult bindingResult,
                                 Model model) {
        if (intervalDataDTO.getFdate() == null) {
            bindingResult.rejectValue("fdate", "error.fdate", "Дата не может быть пустой");
        }
        if (intervalDataDTO.getSdate() == null) {
            bindingResult.rejectValue("sdate", "error.sdate", "Дата не может быть пустой");
        }
        if (intervalDataDTO.getJsonOrCsv() == null) {
            bindingResult.rejectValue("jsonOrCsv", "error.jsonOrCsv", "Необходимо выбрать вариант выдачи данных");
        }
        if (bindingResult.hasErrors()) {
            return "mainexport";
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("fdate", dtf.format(intervalDataDTO.getFdate()));
        model.addAttribute("sdate", dtf.format(intervalDataDTO.getSdate()));
        if (intervalDataDTO.getJsonOrCsv()) {
            return "apifilebackJSON";
        } else {
            model.addAttribute("fdate", intervalDataDTO.getFdate());
            model.addAttribute("sdate", intervalDataDTO.getSdate());
            model.addAttribute("devices", deviceService.findAll());
            return "apifilebackCSV";
        }
    }

    /**
     * Обработка запроса на выдачу в JSON формате
     */
    @GetMapping(value = "/deb.php", params = {"fileback"})
    @ResponseBody
    public Map<String, Entry> loadDataBetweenTextJSON
            (@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fdate,
             @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime sdate) {
        return entryService.getDataBetween(fdate.atZone(ZoneId.systemDefault()).toLocalDateTime(),
                sdate.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    /**
     * Обработка запроса на выдачу в CSV формате
     */
    @GetMapping(value = "/deb.php", params = {"manualmode"})
    public ResponseEntity loadDataBetweenCSV(@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                 LocalDateTime fdate,
                                             @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                 LocalDateTime sdate,
                                             @RequestParam("deviceId") Long deviceId) {
        String str = entryService.getDataBetweenCSV(fdate, sdate, deviceId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=log.csv")
                .contentLength(str.length())
                .header("Content-Type", "text/csv; charset=utf-8")
                .body(str);
    }

    @ExceptionHandler
    private String handleException(EntryNotFoundException e, Model model) {
        model.addAttribute("fdate", e.getFdate());
        model.addAttribute("sdate", e.getSdate());
        model.addAttribute("deviceId", e.getDeviceId());
        model.addAttribute("devices", e.getAllDevices());
        model.addAttribute("dataNotFound", e.getMessage());
        return "apifilebackCSV";
    }
}
