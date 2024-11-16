package com.invensio.cavinator.config.security.util.handler;

public class ErrorResponse {
    private String status;
    private String id;

    public ErrorResponse(String status, String id) {
        this.status = status;
        this.id = id;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
