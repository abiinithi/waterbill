package com.example.waterbill.exception;

import com.example.waterbill.service.ApiLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiLogService logService;

    @ExceptionHandler(WaterBillException.class)
    public ResponseEntity<String> handleWaterBillException(WaterBillException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        // Log to DB even on validation failure
        saveErrorToDb(ex, request);

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("System error: ", ex);

        saveErrorToDb(ex, request);

        return ResponseEntity.internalServerError().body("An internal error occurred.");
    }

    private void saveErrorToDb(Exception ex, HttpServletRequest request) {
        logService.log(
                (String) request.getAttribute("clientName"),
                (String) request.getAttribute("userRole"),
                request.getRequestURI(),
                request.getMethod(),
                "ERROR_CONTEXT",
                null,
                "FAILURE",
                ex.getMessage()
        );
    }
}