package com.mgul.dbrobo.models;

import java.time.LocalDateTime;

public class Time {
    private LocalDateTime fdate;
    private LocalDateTime sdate;
    private String fileback;

    public Time(LocalDateTime fdate, LocalDateTime sdate, String fileback) {
        this.fdate = fdate;
        this.sdate = sdate;
        this.fileback = fileback;
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
