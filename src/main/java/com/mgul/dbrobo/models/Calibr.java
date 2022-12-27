package com.mgul.dbrobo.models;

import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Calibr {
    private String datetime;

    private CalibrData data;


    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public CalibrData getData() {
        return data;
    }

    public void setData(CalibrData data) {
        this.data = data;
    }
}
