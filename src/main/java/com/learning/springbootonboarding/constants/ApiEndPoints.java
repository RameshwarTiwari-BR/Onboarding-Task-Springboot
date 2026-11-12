package com.learning.springbootonboarding.constants;

public class ApiEndPoints {

    private ApiEndPoints() {};

    private static final String API_BASE = "/api";
    private static final String V1 = API_BASE + "/v1";

    // Health Constants
    public static final String HEALTH_V1 = V1 + "/health";

    //    Day 12- Task Crud
    public static final String TASK_V1 = V1 + "/task";

}
