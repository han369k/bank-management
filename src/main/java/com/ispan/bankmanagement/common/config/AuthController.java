package com.ispan.bankmanagement.common.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /** 負責顯示登入畫面 */
    @GetMapping("/login")
    public String showLoginPage() {
        return "customer-login"; // 對應 templates/login.html
    }
}