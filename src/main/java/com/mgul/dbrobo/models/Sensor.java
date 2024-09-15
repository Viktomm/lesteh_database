package com.mgul.dbrobo.models;

import java.util.ArrayList;
import java.util.List;

public class Sensor {
    private String sensor;
    private ArrayList<Calibr> calibr;

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public ArrayList<Calibr> getCalibr() {
        return calibr;
    }

    public void setCalibr(ArrayList<Calibr> calibr) {
        this.calibr = calibr;
    }
}
