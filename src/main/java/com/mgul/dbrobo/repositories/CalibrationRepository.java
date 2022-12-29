package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Calibration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CalibrationRepository extends MongoRepository<Calibration,String> {
    Optional<Calibration> findByuNameAndSerial(String uName,String serial);
}
