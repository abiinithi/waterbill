package com.example.waterbill.model;

public record WaterBillRequest(
        int bhk,
        String ratio,
        int guests
) {}