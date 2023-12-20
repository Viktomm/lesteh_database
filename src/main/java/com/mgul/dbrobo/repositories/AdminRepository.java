package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin,String> {
    Optional<Admin> findByUsername(String username);

    List<Admin> findByRole(String role);
}
