package com.mgul.dbrobo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "devices")
public class Device {
    @Transient
    public static final String SEQUENCE_NAME = "devices_sequence";
    @Id
    private long id;
    @Field("uName")
    private String name;
    private String serial;
    @Field("Akey")
    private String akey;
    @Field("X")
    private double x;

    @Field("Y")
    private double y;

    @Field("Object")
    private String object;

    @Field("Removed")
    private int removed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAkey() {
        return akey;
    }

    public void setAkey(String akey) {
        this.akey = akey;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getRemoved() {
        return removed;
    }

    public void setRemoved(int removed) {

        this.removed = removed;
    }


}
