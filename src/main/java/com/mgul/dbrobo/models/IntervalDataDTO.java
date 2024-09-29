package com.mgul.dbrobo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IntervalDataDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fdate = LocalDateTime.now(ZoneId.systemDefault()).withSecond(0).minusHours(1);

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdate = LocalDateTime.now(ZoneId.systemDefault()).withSecond(0);

    private Boolean jsonOrCsv = true;
}
