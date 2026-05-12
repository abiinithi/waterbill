package com.example.waterbill.exception;

import com.example.waterbill.service.ApiLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiLogService logService;

    // MERGED: Handles all business validation errors
    @ExceptionHandler(WaterBillException.class)
    public ResponseEntity<ErrorResponse> handleValidation(WaterBillException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        saveErrorToDb(ex, request, 400);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // MERGED: Handles all unexpected system crashes
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("System error: ", ex);

        saveErrorToDb(ex, request, 500);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred on our end.",
                System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private void saveErrorToDb(Exception ex, HttpServletRequest request, Integer httpStatus) {
        // Ensure the order of parameters matches your ApiLogService.log() method!
        logService.log(
                (String) request.getAttribute("clientName"),
                (String) request.getAttribute("userRole"),
                request.getRequestURI(),
                request.getMethod(),
                request.toString(),
                null,
                "FAILURE",
                ex.getMessage(),
                httpStatus
        );
    }
}