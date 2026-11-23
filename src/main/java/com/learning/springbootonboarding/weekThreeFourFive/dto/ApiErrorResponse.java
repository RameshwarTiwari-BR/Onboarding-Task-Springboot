package com.learning.springbootonboarding.weekThreeFourFive.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse (
    int statusCode,
    String message,
    String timestamp,
    String requestId,
    Map<String, String> error
){}
