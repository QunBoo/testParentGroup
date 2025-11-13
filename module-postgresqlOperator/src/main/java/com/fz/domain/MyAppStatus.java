package com.fz.domain;

// 定义状态（Status）类
public class MyAppStatus {
    private int availableReplicas;
    private String message;

    // getters 和 setters
    public int getAvailableReplicas() { return availableReplicas; }
    public void setAvailableReplicas(int availableReplicas) {
        this.availableReplicas = availableReplicas;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
