package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.models.Time;
import com.mgul.dbrobo.services.EntryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @GetMapping(value = "/deb.php/text", params = {"fdate", "sdate"}) //
    @ResponseBody
    public Map<String, Entry> loadDataBetweenTextJSON(@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fdate,
                                             @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime sdate) {
        return entryService.getDataBetween(fdate.atZone(ZoneId.systemDefault()).toLocalDateTime(), sdate.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @GetMapping(value = "/deb.php", params = {"fdate", "sdate"}) //
    public String getDataBetween(@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime fdate,
                                       @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime sdate,
                                       Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("fdate", dtf.format(fdate));
        model.addAttribute("sdate", dtf.format(sdate));
        return "apifileback";
    }

    @GetMapping(value = "/deb.php/log.csv", params = {"fdate", "sdate"}, produces = "text/csv")
    public ResponseEntity loadDataBetweenCSV(@RequestParam("fdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime fdate,
                                             @RequestParam("sdate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime sdate) {
        File file = entryService.getDataBetweenCSV(fdate, sdate);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=log.csv")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));
    }

}
