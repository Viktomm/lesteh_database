package com.mgul.dbrobo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Document(collection = "calibration")
public class Calibration {
    @Id
    private String id;

    private String uName;

    private String serial;

    private ArrayList<Sensor> sensors;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public String toString() {
        return "Calibration{" +
                "id='" + id + '\'' +
                ", uName='" + uName + '\'' +
                ", serial='" + serial + '\'' +
                ", sensors=" + sensors +
                '}';
    }
}
