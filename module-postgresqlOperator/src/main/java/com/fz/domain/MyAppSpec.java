package com.fz.domain;

// 定义规格（Spec）类
public class MyAppSpec {
    private int replicas;
    private String image;
    private int port;

    // getters 和 setters
    public int getReplicas() { return replicas; }
    public void setReplicas(int replicas) { this.replicas = replicas; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
}
