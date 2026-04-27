package com.example.waterbill.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "waterbill")
@Getter
@Setter
public class WaterBillConfig {

    private String apiKey;

    private int waterPerPersonPerDay;
    private int daysInMonth;

    private List<Integer> slabCapacities;
    private List<Integer> slabRates;

    private double corporationRate;
    private double borewellRate;

    private Map<Integer, Integer> bhkToResidentCount;
}