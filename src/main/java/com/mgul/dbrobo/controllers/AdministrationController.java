package com.mgul.dbrobo.controllers;

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
    //@ResponseBody
    public String getDevices(HttpServletResponse httpServletResponse, @ModelAttribute("device") Device device, Model model) {
        model.addAttribute("devices",deviceService.findAll());
        httpServletResponse.setContentType("application/javascript");
        return "administration/devices";
        //return ResponseEntity.ok(deviceService.findAll());
    }

    @GetMapping(value = "/devices",params = {"uid"})
    public ResponseEntity getSensorsList(@RequestParam Long uid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin","*");
        headers.add("Access-Control-Allow-Credentials","true");
        return ResponseEntity.ok().headers(headers).body(deviceService.getSensorsList(uid));
    }

    @GetMapping("/devices/get")
    public ResponseEntity getDevices() {
        return ResponseEntity.ok(deviceService.findAll());
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
    public String deleteDevice(@ModelAttribute("device") Device device) {
        deviceService.delete(device);
        return "redirect:/admin/devices";
    }

    @GetMapping("/objects")
    public String getPlaces(@ModelAttribute("place") Place place, Model model) {
        model.addAttribute("objects",placeRepository.findAll());
        return "administration/places";
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

    @DeleteMapping("/objects")
    public String deletePlace(@ModelAttribute("place") Place place) {
        placeRepository.deleteById(place.getId());
        return "redirect:/admin/objects";
    }


    @PutMapping("/calibration")
    public String addCalibration(@ModelAttribute("dto") CalibrationDTO calibrationDTO) {
        calibrationService.save(calibrationDTO);
        return "redirect:/admin/calibration";
    }


    @PostMapping("/multiplecalibration")
    public String addCalibration(@RequestBody List<Calibration> calibrations) {
        calibrationRepository.insert(calibrations);
        return "redirect:/";
    }

    @GetMapping("/calibration")
    public String getCalibration() {
        return "administration/calibr";
    }

    @GetMapping(value = "/calibration",params = {"name","sensor"})
    public String addCalibration(@RequestParam String name, @RequestParam String sensor, Model model) {
//        Calibration calibration;
//        if (calibrationRepository.findByuNameAndSerial(name.split(" ")[0],name.split(" ")[1]).isEmpty()) {
//            calibration = new Calibration();
//            calibration.setuName(name.split(" ")[0]);
//            calibration.setSerial(name.split(" ")[1]);
//            ArrayList<Sensor> sensors = new ArrayList<>();
//            Sensor sensorToAdd=new Sensor();
//            sensorToAdd.setSensor(sensor);
//            ArrayList<Calibr> calibrs = new ArrayList<>();
//            Calibr calibr = new Calibr();
//            calibr.setDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//            CalibrData calibrData = new CalibrData();
//            ArrayList<Double> ai = new ArrayList<>();
//            for (int i = 0; i < 8; i++) {
//                ai.add(0.0);
//            }
//            calibrData.setAi(ai);
//            calibr.setData(calibrData);
//            calibrs.add(calibr);
//            sensorToAdd.setCalibr(calibrs);
//            sensors.add(sensorToAdd);
//            calibration.setSensors(sensors);
//            model.addAttribute("calibration",calibration);
//        } else {
//            calibration = calibrationRepository.findByuNameAndSerial(name.split(" ")[0],name.split(" ")[1]).get();
//            ArrayList<Sensor> sensors = calibration.getSensors();
//            Sensor sensor1 = null;
//            if (sensors==null) {
//                sensors = new ArrayList<>();
//            }
//            for(Sensor s:sensors) {
//                if (s.getSensor().equals(sensor)) {
//                    sensor1=s;
//                    break;
//                }
//            }
//            if (sensor1==null) {
//                sensor1=new Sensor();
//                sensor1.setSensor(sensor);
//                sensors.add(sensor1);
//            }
//            ArrayList<Calibr> calibrs;
//            if (sensor1.getCalibr()!=null) {
//                calibrs=sensor1.getCalibr();
//            } else {
//                calibrs = new ArrayList<>();
//                sensor1.setCalibr(calibrs);
//            }
//            Calibr calibr = new Calibr();
//            calibr.setDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//            CalibrData calibrData = new CalibrData();
//            ArrayList<Double> ai = new ArrayList<>();
//            for (int i = 0; i < 8; i++) {
//                ai.add(0.0);
//            }
//            calibrData.setAi(ai);
//            calibr.setData(calibrData);
//            calibrs.add(calibr);
//            calibration.setSensors(sensors);
//            System.out.println(calibration);
//            model.addAttribute("calibration",calibration);
//        }
//        ArrayList<Sensor> sensors = calibration.getSensors();
//        for(Sensor s:sensors) {
//            if (s.getSensor().equals(sensor)) {
//                model.addAttribute("sensor",sensors.indexOf(s));
//                model.addAttribute("ind",sensors.get(sensors.indexOf(s)).getCalibr().size()-1);
//                break;
//            }
//        }
        CalibrationDTO calibrationDTO = new CalibrationDTO();
        calibrationDTO.setName(name);
        calibrationDTO.setSensor(sensor);
        List<Double> ai = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
                ai.add(0.0);
            }
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
