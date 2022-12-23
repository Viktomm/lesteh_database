package com.mgul.dbrobo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Document(collection = "data")
public class Entry {
    @Id
    private String id;


    @Field("Date")
    private String date;

    private LocalDateTime dateForCalculation;

    private String uName;

    private String serial;
    private LinkedHashMap<String,String> data;

    private String getIdNotForSpring() {
        return id;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private LocalDateTime getDateForCalculation() {
        return dateForCalculation;
    }

    public void setDateForCalculation(LocalDateTime dateForCalculation) {
        this.dateForCalculation = dateForCalculation;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getSerial() {
        return serial;
    }

    public void setData(LinkedHashMap<String, String> data) {
        this.data = data;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
