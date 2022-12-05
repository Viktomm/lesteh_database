package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BasicController {
    private final EntryService entryService;

    @Autowired
    public BasicController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    public String getHomePage(){
        return "index";
    }

    @GetMapping("/debug")
    @ResponseBody
    public ResponseEntity getDebugPage(){
        return ResponseEntity.ok(entryService.firstTenEntries());
    }

    @GetMapping("/mainexport")
    public String getExportPage(){
        return "mainexport";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "/administration/admin";
    }
}
