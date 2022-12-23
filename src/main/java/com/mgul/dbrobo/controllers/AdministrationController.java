package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.models.Admin;
import com.mgul.dbrobo.models.Device;
import com.mgul.dbrobo.services.AdminService;
import com.mgul.dbrobo.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdministrationController {
    private final AdminService adminService;
    private final DeviceService deviceService;

    @Autowired
    public AdministrationController(AdminService adminService, DeviceService deviceService) {
        this.adminService = adminService;
        this.deviceService = deviceService;
    }

    @GetMapping("/edit")
    public String getAdminEditPage(Model model) {
        model.addAttribute("admins",adminService.findAll());
        return "administration/edit";
    }

    @GetMapping("/edit/{id}")
    public String editSomeone(Model model,@PathVariable String id) {
        model.addAttribute(adminService.findById(id));
        return "/administration/update";
    }

    @GetMapping("/new")
    public String newPage(@ModelAttribute("admin") Admin admin) {
        return "/administration/new";
    }

    @PostMapping("/edit")
    public String createAdmin(@ModelAttribute("admin") Admin admin) {
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

    @PostMapping("/pribor")
    public String addNewDevice(@ModelAttribute("device") Device device) {
        deviceService.save(device);
        return "redirect:/admin/pribor";
    }
}
