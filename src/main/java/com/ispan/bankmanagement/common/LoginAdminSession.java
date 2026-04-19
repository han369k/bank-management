package com.ispan.bankmanagement.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ADMIN_SESSION")
@Getter
@Setter
@NoArgsConstructor
public class LoginAdminSession {
    @Id
    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "admin_id", nullable = false)
    private Integer adminId;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;
}