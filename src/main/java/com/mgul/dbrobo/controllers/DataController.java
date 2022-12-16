package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.exceptions.WrongAKeyException;
import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/core")
public class DataController {
    private final EntryService entryService;

    @Autowired
    public DataController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping()
    public ResponseEntity findAll(){
        return ResponseEntity.ok(entryService.findAll());
    }


    @PostMapping("/jsonapp.php")
    public ResponseEntity insertOne(@RequestBody Map<String, Map<String,String>> allData) {
        try {
            entryService.insertOne(allData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (WrongAKeyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
