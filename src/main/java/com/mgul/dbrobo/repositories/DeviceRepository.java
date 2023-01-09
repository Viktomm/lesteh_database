package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Device;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device,String> {
    Optional<Device> findByAkey(String Akey);

    Optional<Device> findById(long id);

    Optional<Device> findByName(String name);

    List<Device> findByNameAndSerialAndXAndYAndObjectAndRemoved(String name, String serial, String x, String y, String object, String removed, Pageable pageable);
}
