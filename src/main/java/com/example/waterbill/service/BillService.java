package com.example.waterbill.service;

import com.example.waterbill.config.WaterBillConfig;
import com.example.waterbill.model.WaterBillRequest;
import com.example.waterbill.model.WaterBillResponse;
import com.example.waterbill.exception.WaterBillException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillService {

    private final WaterBillConfig config;

    // 🔥 MAIN METHOD (Controller will call this)
    public WaterBillResponse calculateBill(WaterBillRequest request) {

        // 1. Validate BHK
        Integer residentCount = config.getBhkToResidentCount().get(request.bhk());
        if (residentCount == null) {
            throw new WaterBillException("Invalid BHK value");
        }

        // 2. Validate and parse ratio
        String ratio = request.ratio();
        if (!ratio.matches("\\d+:\\d+")) {
            throw new WaterBillException("Invalid ratio");
        }

        String[] parts = ratio.split(":");
        int corporationRatio = Integer.parseInt(parts[0]);
        int borewellRatio = Integer.parseInt(parts[1]);
        int totalRatio = corporationRatio + borewellRatio;

        // 3. Validate guests
        int guestCount = request.guests();
        if (guestCount < 0) {
            throw new WaterBillException("Invalid number of guests");
        }

        // 4. Calculate water consumption
        int residentWater = residentCount * config.getWaterPerPersonPerDay() * config.getDaysInMonth();
        int guestWater = guestCount * config.getWaterPerPersonPerDay() * config.getDaysInMonth();

        int totalWater = residentWater + guestWater;

        // 5. Calculate bill
        int guestBill = calculateGuestWaterBill(guestWater);
        int residentBill = calculateResidentWaterBill(
                residentWater,
                corporationRatio,
                borewellRatio,
                totalRatio
        );

        int totalBill = guestBill + residentBill;

        return new WaterBillResponse(totalWater, totalBill);
    }

    // Tanker (guest) water bill using slab logic
    private int calculateGuestWaterBill(int water) {
        int remainingWater = water;
        int totalCost = 0;

        for (int i = 0; i < config.getSlabCapacities().size(); i++) {
            int usage = Math.min(remainingWater, config.getSlabCapacities().get(i));
            totalCost += usage * config.getSlabRates().get(i);
            remainingWater -= usage;

            if (remainingWater <= 0) {
                return totalCost;
            }
        }

        // Remaining water in last slab
        totalCost += remainingWater * config.getSlabRates().getLast();

        return totalCost;
    }

    // Resident water bill based on ratio
    private int calculateResidentWaterBill(
            int water,
            int corporationRatio,
            int borewellRatio,
            int totalRatio
    ) {
        double costPerLiter =
                (corporationRatio * config.getCorporationRate() +
                        borewellRatio * config.getBorewellRate()) / totalRatio;

        return (int) Math.round(water * costPerLiter);
    }
}