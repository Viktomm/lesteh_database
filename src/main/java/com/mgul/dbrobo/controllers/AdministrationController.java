package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.models.Admin;
import com.mgul.dbrobo.models.Calibration;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.models.Place;
import com.mgul.dbrobo.repositories.CalibrationRepository;
import com.mgul.dbrobo.repositories.PlaceRepository;
import com.mgul.dbrobo.services.AdminService;
import com.mgul.dbrobo.services.DeviceService;
import com.mgul.dbrobo.services.EntryService;
import com.mgul.dbrobo.services.generators.SequenceGeneratorService;
import com.mgul.dbrobo.utils.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdministrationController {
    private final AdminService adminService;
    private final DeviceService deviceService;
    private final PlaceRepository placeRepository;

    private final SequenceGeneratorService sequenceGeneratorService;

    private final CalibrationRepository calibrationRepository;

    private final EntryService entryService;

    @Autowired
    public AdministrationController(AdminService adminService, DeviceService deviceService, PlaceRepository placeRepository, SequenceGeneratorService sequenceGeneratorService, CalibrationRepository calibrationRepository, EntryService entryService) {
        this.adminService = adminService;
        this.deviceService = deviceService;
        this.placeRepository = placeRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.calibrationRepository = calibrationRepository;
        this.entryService = entryService;
    }

    @GetMapping("/edit")
    public String getAdminEditPage(Model model) {
        model.addAttribute("admins",adminService.findAll());
        return "administration/edit";
    }

    @GetMapping("/edit/{id}")
    public String editSomeone(Model model,@PathVariable String id) {
        model.addAttribute(adminService.findById(id));
        model.addAttribute("roles", Roles.values());
        return "/administration/update";
    }

    @GetMapping("/new")
    public String newPage(@ModelAttribute("admin") Admin admin,Model model) {
        model.addAttribute("roles", Roles.values());
        return "/administration/new";
    }

    @PostMapping("/edit")
    public String createAdmin(@ModelAttribute("admin") Admin admin, Model model){
        adminService.save(admin);
        return "redirect:/admin/edit";
    }

    @PatchMapping("/edit/{id}")
    public String updateSomeone(@PathVariable String id,
                                        @ModelAttribute("admin") Admin admin) {
        adminService.update(id,admin);
        return "redirect:/admin/edit";
    }

    @DeleteMapping("/edit/{id}")
    public String deleteSomeone(@PathVariable String id) {
        adminService.delete(id);
        return "redirect:/admin/edit";
    }

    @GetMapping("/insert")
    public String addingData() {
        return "administration/insert";
    }

    @PostMapping("/insert")
    public String insertMany(@RequestBody List<LinkedHashMap<String, LinkedHashMap<String, String>>> allData) {
        entryService.insertMany(allData);
        return "redirect:/admin";
    }

    @GetMapping("/devices")
    public String getDevices(@ModelAttribute("device") Device device,Model model) {
        model.addAttribute("devices",deviceService.findAll());
        return "administration/devices";
    }

    @PostMapping("/devices")
    public String addNewDevice(@ModelAttribute("device") Device device) {
        device.setId(sequenceGeneratorService.generateSequence(Device.SEQUENCE_NAME));
        device.setObject(placeRepository.findById(device.getObjId()).get().getName());
        deviceService.save(device);
        return "redirect:/admin/devices";
    }

    @PatchMapping("/devices")
    public String updateDevice(@ModelAttribute("device") Device device) {
        deviceService.update(device);
        return "redirect:/admin/devices";
    }

    @DeleteMapping("/devices")
    public String deleteDevice(@ModelAttribute("device") Device device) {
        deviceService.delete(device);
        return "redirect:/admin/devices";
    }

    @GetMapping("/objects")
    public String getPlaces(@ModelAttribute("place") Place place, Model model) {
        model.addAttribute("objects",placeRepository.findAll());
        return "administration/places";
    }

    @PostMapping("/objects")
    public String addPlace(@ModelAttribute("place") Place place) {
        place.setId(sequenceGeneratorService.generateSequence(Place.SEQUENCE_NAME));
        placeRepository.insert(place);
        return "redirect:/admin/objects";
    }

    @DeleteMapping("/objects")
    public String deletePlace(@ModelAttribute("place") Place place) {
        placeRepository.deleteById(place.getId());
        return "redirect:/admin/objects";
    }


    @PostMapping("/calibration")
    public String addCalibration(@RequestBody Calibration calibration) {
        calibrationRepository.insert(calibration);
        return "redirect:/";
    }

    @PostMapping("/multiplecalibration")
    public String addCalibration(@RequestBody List<Calibration> calibrations) {
        calibrationRepository.insert(calibrations);
        return "redirect:/";
    }

    @GetMapping("/calibration")
    @ResponseBody
    public ResponseEntity getCalibration() {
        return ResponseEntity.ok(calibrationRepository.findAll());
    }


    @GetMapping("/accessDenied")
    public String getAccessDeniedPage() {
        return "administration/accessDenied";
    }
}
