
package com.learning.springbootonboarding.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Generate a unique ID
        String requestId = UUID.randomUUID().toString();

        // 2. Add ID to the response header
        httpResponse.setHeader("X-Request-ID", requestId);

        // 3. Store ID in MDC so logs can include it
        MDC.put("requestId", requestId);

        long startTime = System.nanoTime();

        try {
            logger.info("Request started: {} {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI());

            // Continue to controller and downstream processing
            chain.doFilter(request, response);

        } finally {
            long duration = (System.nanoTime() - startTime) / 1_000_000;

            logger.info("Request completed: {} {} | Status: {} | Duration: {} ms",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    duration);

            // Important: clean up thread-local logging context
            MDC.remove("requestId");
        }
    }
}
