package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.exceptions.WrongPasswordException;
import com.mgul.dbrobo.models.*;
import com.mgul.dbrobo.repositories.CalibrationRepository;
import com.mgul.dbrobo.repositories.PlaceRepository;
import com.mgul.dbrobo.services.AdminService;
import com.mgul.dbrobo.services.CalibrationService;
import com.mgul.dbrobo.services.DeviceService;
import com.mgul.dbrobo.services.EntryService;
import com.mgul.dbrobo.services.generators.SequenceGeneratorService;
import com.mgul.dbrobo.utils.Roles;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    private final CalibrationService calibrationService;

    @Autowired
    public AdministrationController(AdminService adminService, DeviceService deviceService, PlaceRepository placeRepository, SequenceGeneratorService sequenceGeneratorService, CalibrationRepository calibrationRepository, EntryService entryService, CalibrationService calibrationService) {
        this.adminService = adminService;
        this.deviceService = deviceService;
        this.placeRepository = placeRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.calibrationRepository = calibrationRepository;
        this.entryService = entryService;
        this.calibrationService = calibrationService;
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

    public String createAdmin(@ModelAttribute("admin") Admin admin,@RequestParam String passcopy, Model model){
        if (!admin.getPassword().equals(passcopy)) {
            throw new WrongPasswordException("Пароли не совпадают");
        }
        adminService.save(admin);
        return "redirect:/admin/edit";
    }

    @PatchMapping("/edit/{id}")
    public String updateSomeone(@PathVariable String id,
                                        @ModelAttribute("admin") Admin admin,
                                @RequestParam String passcopy) {
        if (!admin.getPassword().equals(passcopy)) {
            throw new WrongPasswordException("Пароли не совпадают");
        }
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
    public String insertMany(@RequestParam("file") MultipartFile file) {
        entryService.saveDataFromFile(file);
        return "redirect:/admin";
    }

    @GetMapping("/devices/new")
    public String addDevice(Model model) {
        model.addAttribute("newDev",new Device());
        return "administration/addDevice";
    }

    @GetMapping("/devices")
    public String getDevices(@ModelAttribute("device") Device device) {
        return "administration/devices";
    }

    @GetMapping(value = "/devices",params = {"name","serial","x","y","object","removed"})
    public String getDevicesWithParams(@RequestParam String name,@RequestParam String serial,@RequestParam String x,
                                       @RequestParam String y,@RequestParam String object,@RequestParam String removed,@RequestParam(defaultValue = "1") int page,Model model) {
        return "administration/devices";
    }

    @GetMapping(value="/devices/get",params = {"name","serial","x","y","object","removed"})
    public ResponseEntity getDevices(@RequestParam String name,@RequestParam String serial,@RequestParam String x,
                                     @RequestParam String y,@RequestParam String object,@RequestParam String removed,@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(deviceService.findByParams(name,serial,x,y,object,removed,page));
    }
    @GetMapping("/devices/getall")
    public ResponseEntity getAllDevices() {
        return ResponseEntity.ok(deviceService.findAll());
    }
    @GetMapping(value = "/devices/get")
    public ResponseEntity getDevicesWOParams(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(deviceService.findByParams("","","","","","",page));
    }
    @GetMapping("/devices/getNumberOfPages")
    public ResponseEntity getNum(@RequestParam String name,@RequestParam String serial,@RequestParam String x,
                                 @RequestParam String y,@RequestParam String object,@RequestParam String removed) {
        return ResponseEntity.ok(deviceService.getCount(name,serial,x,y,object,removed));
    }
    @GetMapping(value = "/devices",params = {"uid"})
    public ResponseEntity getSensorsList(@RequestParam Long uid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin","*");
        headers.add("Access-Control-Allow-Credentials","true");
        return ResponseEntity.ok().headers(headers).body(deviceService.getSensorsList(uid));
    }


    @GetMapping("/devices/edit")
    public String getEditDevicePage(@RequestParam Long id,Model model){
        model.addAttribute("device",deviceService.findById(id));
        return "administration/editDevice";
    }

    @PostMapping("/devices")
    public String addNewDevice(@ModelAttribute("device") Device device) {
        device.setId(sequenceGeneratorService.generateSequence(Device.SEQUENCE_NAME));
        deviceService.save(device);
        return "redirect:/admin/devices";
    }

    @PatchMapping("/devices")
    public String updateDevice(@ModelAttribute("device") Device device) {
        deviceService.update(device);
        return "redirect:/admin/devices";
    }

    @DeleteMapping("/devices")
    public String deleteDevice(@RequestParam("id") Long id) {
        deviceService.delete(id);
        return "redirect:/admin/devices";
    }

    @GetMapping("/objects")
    public String getPlaces(@ModelAttribute("place") Place place, Model model) {
        model.addAttribute("objects",placeRepository.findAll());
        return "administration/places";
    }
    @GetMapping("/objects/{id}")
    public String getEditPlace(@PathVariable Long id,Model model) {
        model.addAttribute("place",placeRepository.findById(id));
        return "administration/editObject";
    }
    @GetMapping("/objects/new")
    public String addNewObject(Model model) {
        model.addAttribute("place",new Place());
        return "administration/addObject";
    }
    @PatchMapping("/objects")
    public String updatePlace(@ModelAttribute Place place) {
        placeRepository.save(place);
        return "redirect:/admin/objects";
    }
    @GetMapping("/objects/get")
    public ResponseEntity getPlacesForJs() {
        return ResponseEntity.ok(placeRepository.findAll());
    }

    @PostMapping("/objects")
    public String addPlace(@ModelAttribute("place") Place place) {
        place.setId(sequenceGeneratorService.generateSequence(Place.SEQUENCE_NAME));
        placeRepository.insert(place);
        return "redirect:/admin/objects";
    }

    @DeleteMapping("/objects/{id}")
    public String deletePlace(@PathVariable Long id) {
        placeRepository.deleteById(id);
        return "redirect:/admin/objects";
    }

    @PostMapping("/multiplecalibration")
    public String addCalibration(@RequestBody List<Calibration> calibrations) {
        calibrationRepository.insert(calibrations);
        return "redirect:/";
    }


    @PutMapping("/calibration")
    public String addCalibration(@ModelAttribute("dto") CalibrationDTO calibrationDTO) {
        calibrationService.save(calibrationDTO);
        return "redirect:/admin/calibration";
    }
    @GetMapping("/calibration")
    public String getCalibration() {
        return "administration/calibr";
    }

    @GetMapping(value = "/calibration",params = {"name","sensor"})
    public String addCalibration(@RequestParam String name, @RequestParam String sensor, Model model) {
        CalibrationDTO calibrationDTO = new CalibrationDTO();
        calibrationDTO.setName(name);
        calibrationDTO.setSensor(sensor);
        List<Double> ai = new ArrayList<>();
        calibrationDTO.setAi(ai);
        model.addAttribute("dto",calibrationDTO);
        return "administration/addCalibr";
    }

    @GetMapping("/calibration/get")
    public ResponseEntity getCalibrData() {
        return ResponseEntity.ok(calibrationRepository.findAll());
    }
    @GetMapping("/accessDenied")
    public String getAccessDeniedPage() {
        return "administration/accessDenied";
    }
}
