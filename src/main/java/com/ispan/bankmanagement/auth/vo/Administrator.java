package com.ispan.bankmanagement.auth.vo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Administrator implements Serializable {
    private static final long serialVersionUID = 1L;

    // 這裡我們維持 Java 的命名規範（駝峰式）
    private Integer adminId;    
    private String username;
    private String passwordHash;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 空建構子 (這一定要有，給 Java 框架用的)
    public Administrator() {}

    // Getter 與 Setter (請在 Eclipse 按 Alt+Shift+S > r 生成)
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}