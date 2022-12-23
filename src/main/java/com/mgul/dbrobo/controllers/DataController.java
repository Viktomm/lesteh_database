package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/core")
public class DataController {
    private final EntryService entryService;

    @Autowired
    public DataController(EntryService entryService) {
        this.entryService = entryService;
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

    @GetMapping(value = "/deb.php", params = {"fdate", "sdate"})
    @ResponseBody
    public Map<String, Entry> getDataBetween(@RequestParam(value = "fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fdate,
                                             @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date sdate) {
        return entryService.getDataBetween(fdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), sdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @GetMapping(value = "/deb.php", params = {"fdate", "sdate", "fileback"})
    public String getDataBetweenInFile(@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fdate,
                                       @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date sdate,
                                       @RequestParam(value = "fileback") String fileback,
                                       Model model) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("fdate", format.format(fdate));
        model.addAttribute("sdate", format.format(sdate));
        return "apifileback";
    }

}
