package com.dashboard.stocktracker.dto;

public record DailyStockResponse (
        String data,
        double open,
        double close,
        double high,
        double low,
        long volume
){}
