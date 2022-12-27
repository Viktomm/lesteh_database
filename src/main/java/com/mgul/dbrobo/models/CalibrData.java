package com.mgul.dbrobo.models;

import java.util.List;

public class CalibrData {
    private long n;
    private List<Double> ai;

    public long getN() {
        return n;
    }

    public void setN(long n) {
        this.n = n;
    }

    public List<Double> getAi() {
        return ai;
    }

    public void setAi(List<Double> ai) {
        this.ai = ai;
    }
}
