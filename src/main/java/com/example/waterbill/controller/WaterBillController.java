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

        String endpoint = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        try {
            WaterBillResponse response = billService.calculateBill(request);

            logService.log(
                    endpoint,
                    method,
                    request.toString(),
                    response.toString(),
                    "SUCCESS",
                    null
            );

            return response;

        } catch (Exception e) {

            logService.log(
                    endpoint,
                    method,
                    request.toString(),
                    null,
                    "FAILURE",
                    e.getMessage()
            );

            throw e;
        }
    }

}