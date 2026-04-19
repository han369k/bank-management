package com.ispan.bankmanagement.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ADMINISTRATOR")
@Getter
@Setter
@NoArgsConstructor
public class LoginAdministrator {

    @Id
    @Column(name = "admin_id")
    private Integer adminId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(length = 50)
    private String name;

    @Column(length = 10)
    private String status;
}