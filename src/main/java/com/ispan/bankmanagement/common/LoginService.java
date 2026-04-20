package com.ispan.bankmanagement.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final LoginAdministratorRepository administratorRepository;
    private final LoginAdminSessionRepository adminSessionRepository;

    @Transactional
    public String login(String username, String password, String ipAddress) {
        LoginAdministrator admin = administratorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("帳號或密碼錯誤"));

        if ("SUSPENDED".equals(admin.getStatus())) {
            throw new RuntimeException("該帳號已被停權，請聯絡系統管理員");
        }

        if (!password.equals(admin.getPasswordHash())) {
            throw new RuntimeException("帳號或密碼錯誤");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        LoginAdminSession session = new LoginAdminSession();
        session.setSessionId(token);
        session.setAdminId(admin.getAdminId());
        session.setLoginTime(now);
        session.setExpireTime(now.plusHours(8)); // 預設 8 小時後過期
        session.setIpAddress(ipAddress);
        
        adminSessionRepository.save(session);
        log.info("行員 {} 登入成功，配發 Token: {}", username, token);
        
        return token;
    }

    @Transactional
    public void logout(String token) {
        adminSessionRepository.findById(token).ifPresent(adminSessionRepository::delete);
    }

    public boolean validateSession(String token) {
        if (token == null || token.isBlank()) return false;
        return adminSessionRepository.findById(token)
                .map(session -> session.getExpireTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}