package com.mgul.dbrobo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Document(collection = "data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entry {
    @Id
    private String id;
    @Field("Date")
    private String date;
    private LocalDateTime dateForCalculation;
    @Field("uName")
    private String uName;
    private String serial;
    private LinkedHashMap<String,String> data;

    private String getIdNotForSpring() {
        return id;
    }
}
