package com.mgul.dbrobo.controllers;

import com.mgul.dbrobo.models.Admin;
import com.mgul.dbrobo.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdministrationController {
    private final AdminService adminService;

    @Autowired
    public AdministrationController(AdminService adminService) {
        this.adminService = adminService;
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
}
