package com.mgul.dbrobo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DebugFilters {
    private String uName = "";
    private String serial = "";
    private Boolean whichDate = true;
}
