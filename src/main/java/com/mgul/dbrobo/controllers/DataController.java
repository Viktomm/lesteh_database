package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.models.Entry;
import com.mgul.dbrobo.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/test")
public class DataController {
    private final EntryService entryService;

    @Autowired
    public DataController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    public ResponseEntity findAll(){
        return ResponseEntity.ok(entryService.findAll());
    }

    @PostMapping
    public ResponseEntity insertOne(@RequestBody Entry entry) {
        entry.setCreatedAt(LocalDateTime.now());
        entryService.insertOne(entry);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
