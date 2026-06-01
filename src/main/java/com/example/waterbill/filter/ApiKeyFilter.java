package com.example.waterbill.filter;

import com.example.waterbill.entity.ApiKey;
import com.example.waterbill.exception.ErrorResponse;
import com.example.waterbill.repository.ApiKeyRepository;
import com.example.waterbill.service.ApiLogService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiLogService logService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Prevent double logging: Skip if this is an internal error dispatch
        if (DispatcherType.ERROR.equals(request.getDispatcherType())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String keyValue = request.getHeader("x-api-key");

        if (keyValue == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                keyValue = authHeader.substring(7);
            }
        }

        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyValueAndActiveTrue(keyValue);

        // CASE 1: Actuator Access (Strictly for ADMIN)
        if (path.contains("actuator")) {
            if (apiKeyOpt.isPresent() && "ADMIN".equals(apiKeyOpt.get().getRole().name())) {
                logToDatabase(request, "SUCCESS", "Actuator Access", apiKeyOpt.get(), null, 200);
                filterChain.doFilter(request, response);
                return;
            } else {
                String roleName = apiKeyOpt.map(k -> k.getRole().name()).orElse("GUEST");
                log.error("Access Denied: Role {} tried to access metrics.", roleName);

                logToDatabase(request, "FAILURE", null, apiKeyOpt.orElse(null), "Forbidden: Non-admin metrics access", 403);
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admins Only");
                return;
            }
        }

        // CASE 2: Invalid/Missing Key for other protected endpoints
        if (apiKeyOpt.isEmpty()) {
            logToDatabase(request, "FAILURE", null, null, "Unauthorized: Invalid API Key", 401);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Missing API Key");
            return;
        }

        // SUCCESS CASE: Set attributes and proceed to Controller
        ApiKey key = apiKeyOpt.get();
        request.setAttribute("userRole", key.getRole().name());
        request.setAttribute("clientName", key.getClientName());

        filterChain.doFilter(request, response);
    }

    /**
     * Aligned with ApiLogService.log order:
     * (endpoint, method, requestBody, responseBody, status, error, role, clientName)
     */
    private void logToDatabase(HttpServletRequest request, String status, String response, ApiKey key, String error, Integer httpStatus) {

        logService.log(
                key != null ? key.getClientName() : "UNKNOWN",  // client_name
                key != null ? key.getRole().name() : "GUEST",  // role
                request.getRequestURI(),
                request.getMethod(),
                "Filter Interception",                 // requestBody (fixed label)
                response,                                         // responseBody (detailed note)
                status,                                       // status
                error,
                httpStatus
        );
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        // Create the record manually
        ErrorResponse errorBody = new ErrorResponse(
                status,
                status == 401 ? "Unauthorized" : "Forbidden",
                message,
                System.currentTimeMillis()
        );

        // Convert to JSON string (Manually formatting to avoid adding more dependencies)
        String jsonResponse = String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"timestamp\": %d}",
                errorBody.status(),
                errorBody.error(),
                errorBody.message(),
                errorBody.timestamp()
        );

        response.getWriter().write(jsonResponse);
    }
}