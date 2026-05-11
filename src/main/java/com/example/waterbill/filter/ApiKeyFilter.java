package com.example.waterbill.filter;

import com.example.waterbill.config.WaterBillConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final WaterBillConfig config;

    private static final String HEADER_NAME = "x-api-key";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Allow H2 console without API key
        if (path.contains("h2-console") || path.contains("actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(HEADER_NAME);

        // Validate API key
        if (apiKey == null || !apiKey.equals(config.getApiKey())) {
            log.warn("Unauthorized access attempt from IP: {} on path: {}",
                    request.getRemoteAddr(), path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or missing API Key");
            return;
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}