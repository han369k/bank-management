package com.ispan.bankmanagement.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private static final String COOKIE_NAME = "ADMIN_SESSION_ID";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String ipAddress = req.getRemoteAddr();
            String token = loginService.login(request.username(), request.password(), ipAddress);

            Cookie cookie = new Cookie(COOKIE_NAME, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(8 * 60 * 60);
            resp.addCookie(cookie);

            return ResponseEntity.ok(Map.of("message", "登入成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = COOKIE_NAME, required = false) String token, HttpServletResponse resp) {
        if (token != null) loginService.logout(token);
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }
}