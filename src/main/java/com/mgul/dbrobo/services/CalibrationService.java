package com.mgul.dbrobo.services;

import com.mgul.dbrobo.models.*;
import com.mgul.dbrobo.repositories.CalibrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CalibrationService {
    private final CalibrationRepository calibrationRepository;

    @Autowired
    public CalibrationService(CalibrationRepository calibrationRepository) {
        this.calibrationRepository = calibrationRepository;
    }

    public void save(CalibrationDTO calibrationDTO) {
        Calibration calibration;
        if (calibrationRepository.findByuNameAndSerial(calibrationDTO.getName().split("::")[0],calibrationDTO.getName().split("::")[1]).isEmpty()) {
            calibration = new Calibration();
            calibration.setuName(calibrationDTO.getName().split("::")[0]);
            calibration.setSerial(calibrationDTO.getName().split("::")[1]);
            ArrayList<Sensor> sensors = new ArrayList<>();
            Sensor sensorToAdd=new Sensor();
            sensorToAdd.setSensor(calibrationDTO.getSensor());
            ArrayList<Calibr> calibrs = new ArrayList<>();
            Calibr calibr = new Calibr();
            calibr.setDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            CalibrData calibrData = new CalibrData();
            calibrData.setAi(calibrationDTO.getAi());
            calibrData.setN(calibrationDTO.getN());
            calibr.setData(calibrData);
            calibrs.add(calibr);
            sensorToAdd.setCalibr(calibrs);
            sensors.add(sensorToAdd);
            calibration.setSensors(sensors);
        } else {
            calibration = calibrationRepository.findByuNameAndSerial(calibrationDTO.getName().split("::")[0],calibrationDTO.getName().split("::")[1]).get();
            ArrayList<Sensor> sensors = calibration.getSensors();
            Sensor sensor1 = null;
            if (sensors==null) {
                sensors = new ArrayList<>();
            }
            for(Sensor s:sensors) {
                if (s.getSensor().equals(calibrationDTO.getSensor())) {
                    sensor1=s;
                    break;
                }
            }
            if (sensor1==null) {
                sensor1=new Sensor();
                sensor1.setSensor(calibrationDTO.getSensor());
                sensors.add(sensor1);
            }
            ArrayList<Calibr> calibrs;
            if (sensor1.getCalibr()!=null) {
                calibrs=sensor1.getCalibr();
            } else {
                calibrs = new ArrayList<>();
                sensor1.setCalibr(calibrs);
            }
            Calibr calibr = new Calibr();
            calibr.setDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            CalibrData calibrData = new CalibrData();
            calibrData.setAi(calibrationDTO.getAi());
            calibrData.setN(calibrationDTO.getN());
            calibr.setData(calibrData);
            calibrs.add(calibr);
            calibration.setSensors(sensors);
        }
        calibrationRepository.save(calibration);
    }

    public List<String> findSensorsByUnameAndSerial(String uName, String serial) {
        Optional<Calibration> cal = calibrationRepository.findByuNameAndSerial(uName, serial);
        if (cal.isEmpty()) return new ArrayList<>();
        Calibration calibration = cal.get();
        return calibration.getSensorsNames();
    }

}
