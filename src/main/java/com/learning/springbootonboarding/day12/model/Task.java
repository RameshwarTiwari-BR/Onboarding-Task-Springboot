package com.learning.springbootonboarding.day12.model;

import java.time.LocalDateTime;

public class Task {
    private Long id;
    private String name;
    private boolean status;
    private LocalDateTime timestamp;

    // Constructor
    public Task(Long id, String name, boolean status, LocalDateTime timestamp) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and Setters (Encapsulation)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }


}
