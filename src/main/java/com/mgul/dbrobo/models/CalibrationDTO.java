package com.mgul.dbrobo.models;


import java.util.List;

public class CalibrationDTO {
    private String name;
    private String sensor;
    private int n;
    private List<Double> ai;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public List<Double> getAi() {
        return ai;
    }

    public void setAi(List<Double> ai) {
        this.ai = ai;
    }
}
