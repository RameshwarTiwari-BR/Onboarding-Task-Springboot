package com.learning.springbootonboarding.constants;

public class ApiEndPoints {

    private ApiEndPoints() {};

    private static final String API_BASE = "/api";
    private static final String V1 = API_BASE + "/v1";
    private static final String AUTH_V1 = V1 + "/auth";

    // Health Constants
    public static final String HEALTH_V1 =  "/actuator/health";

    //    Day 12- Task Crud
    public static final String TASK_V1 = V1 + "/tasks";
    public static final String PROJECT_V1 = V1 + "/projects";

    public static final String AUTH_SIGNUP_V1 = AUTH_V1 + "/signup";
    public static final String AUTH_LOGIN_V1 = AUTH_V1 + "/login";
    public static final String AUTH_LOGOUT_V1 = AUTH_V1 + "/logout";

}
