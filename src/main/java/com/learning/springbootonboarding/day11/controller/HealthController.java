package com.learning.springbootonboarding.day11.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.learning.springbootonboarding.constants.ApiEndPoints;
import com.learning.springbootonboarding.day11.dto.HealthCheckResponse;
import java.time.LocalDateTime;

@RestController
public class HealthController {

    @GetMapping(ApiEndPoints.HEALTH_V1)
    public ResponseEntity<HealthCheckResponse>health(){
        HealthCheckResponse payload = new HealthCheckResponse(
                "UP",
                "Application is running",
                LocalDateTime.now().toString()
        );
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }
}
