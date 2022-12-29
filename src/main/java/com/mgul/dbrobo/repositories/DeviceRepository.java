package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device,String> {
    Optional<Device> findByAkey(String Akey);

    @Override
    Page<Device> findAll(Pageable pageable);
}
