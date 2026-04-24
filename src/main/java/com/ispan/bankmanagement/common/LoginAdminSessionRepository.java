package com.ispan.bankmanagement.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAdminSessionRepository extends JpaRepository<LoginAdminSession, String> {
}