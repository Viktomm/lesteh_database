package com.mgul.dbrobo.repositories;

import com.mgul.dbrobo.models.Calibration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CalibrationRepository extends MongoRepository<Calibration,String> {
}
