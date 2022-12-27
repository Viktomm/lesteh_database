package com.mgul.dbrobo.services;

import com.mgul.dbrobo.repositories.CalibrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalibrationService {
    private final CalibrationRepository calibrationRepository;

    @Autowired
    public CalibrationService(CalibrationRepository calibrationRepository) {
        this.calibrationRepository = calibrationRepository;
    }

}
