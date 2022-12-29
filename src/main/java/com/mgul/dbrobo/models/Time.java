package com.mgul.dbrobo.models;

import java.time.LocalDateTime;

public class Time {
    private LocalDateTime fdate;
    private LocalDateTime sdate;
    private String fileback;

    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Time(LocalDateTime fdate, LocalDateTime sdate, String fileback, String deviceName) {
        this.fdate = fdate;
        this.sdate = sdate;
        this.fileback = fileback;
        this.deviceName = deviceName;
    }


    public LocalDateTime getFdate() {
        return fdate;
    }

    public void setFdate(LocalDateTime fdate) {
        this.fdate = fdate;
    }

    public LocalDateTime getSdate() {
        return sdate;
    }

    public void setSdate(LocalDateTime sdate) {
        this.sdate = sdate;
    }

    public String getFileback() {
        return fileback;
    }

    public void setFileback(String fileback) {
        this.fileback = fileback;
    }
}
