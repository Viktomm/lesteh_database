package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.models.DebugFilters;
import com.mgul.dbrobo.models.IntervalDataDTO;
import com.mgul.dbrobo.services.DeviceService;
import com.mgul.dbrobo.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

@Controller
public class BasicController {
    private final EntryService entryService;
    private final DeviceService deviceService;

    @Autowired
    public BasicController(EntryService entryService, DeviceService deviceService) {
        this.entryService = entryService;
        this.deviceService = deviceService;
    }

    @GetMapping
    public String getHomePage(){
        return "index";
    }

    @GetMapping("/debug")
    public String getDebugPage(@ModelAttribute("debugFilters") DebugFilters debugFilters,
                               Model model) {
        model.addAttribute("entries", entryService.lastTenEntries(debugFilters.getUName().strip(), debugFilters.getSerial().strip(), debugFilters.getWhichDate()));
        return "debug";
    }

    @GetMapping("/mainexport")
    public String getExportPage(@ModelAttribute("intervalData") IntervalDataDTO intervalDataDTO){
        return "mainexport";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "administration/admin";
    }
}
