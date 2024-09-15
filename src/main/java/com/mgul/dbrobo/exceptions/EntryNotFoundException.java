package com.mgul.dbrobo.exceptions;

import com.mgul.dbrobo.models.Device;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class EntryNotFoundException extends RuntimeException {
    LocalDateTime fdate;
    LocalDateTime sdate;
    Long deviceId;
    List<Device> allDevices;

    public EntryNotFoundException(LocalDateTime fdate, LocalDateTime sdate, Long deviceId, List<Device> all, String message) {
        super(message);
        this.fdate = fdate;
        this.sdate = sdate;
        this.deviceId = deviceId;
        allDevices = all;
    }
}
