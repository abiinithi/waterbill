package com.example.waterbill.controller;

import com.example.waterbill.model.WaterBillRequest;
import com.example.waterbill.model.WaterBillResponse;
import com.example.waterbill.service.ApiLogService;
import com.example.waterbill.service.BillService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/waterbill")
@RequiredArgsConstructor
public class WaterBillController {

    private final BillService billService;
    private final ApiLogService logService;

    @PostMapping("/calculate")
    public WaterBillResponse calculate(
            @RequestBody WaterBillRequest request,
            HttpServletRequest httpRequest) {

        // Retrieve attributes set by the filter
        String role = (String) httpRequest.getAttribute("userRole");
        String clientName = (String) httpRequest.getAttribute("clientName");

        // Logic
        WaterBillResponse response = billService.calculateBill(request);

        // Success Log
        logService.log(
                clientName,
                role,
                httpRequest.getRequestURI(),
                httpRequest.getMethod(),
                request.toString(),
                response.toString(),
                "SUCCESS",
                null,
                200
        );

        // Role-based masking
        return "VIEWER".equals(role)
                ? new WaterBillResponse(0, response.totalBill())
                : response;
    }
}