package com.mgul.dbrobo.services;

import com.mgul.dbrobo.exceptions.AdminNotFoundException;
import com.mgul.dbrobo.exceptions.LastAdminException;
import com.mgul.dbrobo.exceptions.WrongPasswordException;
import com.mgul.dbrobo.models.Admin;
import com.mgul.dbrobo.repositories.AdminRepository;
import com.mgul.dbrobo.utils.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findById(String id) {
        return adminRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Admin admin) {
        adminRepository.insert(admin);
    }
    @Transactional
    public void delete(String id) {
        Optional<Admin> adm = adminRepository.findById(id);
        if (adm.isPresent()) {
            if (adm.get().getRole().equals("ROLE_ADMIN") && adminRepository.findByRole("ROLE_ADMIN").size()==1) {
                throw new LastAdminException("This is the last admin in database");
            } else {
                adminRepository.delete(adm.get());
            }
        }
            else throw new AdminNotFoundException("No admin with such id");
    }

    @Transactional
    public void update(String id, Admin newAdminData) {
        Admin a = adminRepository.findById(id).get();
        if (newAdminData.getUsername()!=null) a.setUsername(newAdminData.getUsername());
        if (!newAdminData.getPassword().isEmpty()) a.setPassword(newAdminData.getPassword());
        if (newAdminData.getFio()!=null) a.setFio(newAdminData.getFio());
        a.setRole(newAdminData.getRole());
        adminRepository.save(a);
    }
}
