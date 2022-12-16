package com.mgul.dbrobo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "devices")
public class Device {
    @Id
    private String id;

    private String name;
    @Field("Akey")
    private String akey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
