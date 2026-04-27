package com.example.waterbill.service;

import com.example.waterbill.entity.ApiLog;
import com.example.waterbill.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.lang.reflect.*;

@Service
@RequiredArgsConstructor
public class ApiLogService {

    private final ApiLogRepository repository;

    public void log(String endpoint,
                    String method,
                    String request,
                    String response,
                    String status,
                    String error) {

        ApiLog log = new ApiLog();
        log.setEndpoint(endpoint);
        log.setMethod(method);
        log.setRequestBody(request);
        log.setResponseBody(response);
        log.setStatus(status);
        log.setErrorMessage(error);
        log.setTimestamp(LocalDateTime.now());

        repository.save(log);
    }
}