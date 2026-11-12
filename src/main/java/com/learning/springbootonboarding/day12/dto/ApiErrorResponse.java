package com.learning.springbootonboarding.day12.dto;

import java.util.Map;

public record ApiErrorResponse (
    int statusCode,
    String message,
    String timestamp,
    String requestId,
    Map<String, String> error
){}
