package com.ispan.bankmanagement.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全防護設定中心
 * 負責管控哪些網頁需要登入、登入頁面在哪裡，以及登出的流程。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 關閉 CSRF 防護 (開發與測試階段先關閉較方便，正式上線時建議開啟並配合 Thymeleaf 標籤)
            .csrf(csrf -> csrf.disable())
            
            // 2. 設定網址的存取權限
            .authorizeHttpRequests(auth -> auth
                // ✨ 關鍵：放行登入頁、錯誤頁 (避免無限迴圈)、以及前端靜態資源 (CSS/JS/圖片)
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**").permitAll() 
                // 其他所有的網址 (包含 /customer) 都必須經過登入驗證
                .anyRequest().authenticated()
            )
            
            // 3. 設定表單登入機制
            .formLogin(form -> form
                .loginPage("/login")                  // 告訴系統：我們自訂的登入畫面在哪裡
                .defaultSuccessUrl("/customer", true) // 登入成功後，強制導向「顧客清單」頁面
                .permitAll()                          // 允許所有人執行登入動作
            )
            
            // 4. 設定登出機制
            .logout(logout -> logout
                .logoutUrl("/logout")                 // 觸發登出的網址 (前端只要呼叫這個網址就會登出)
                .logoutSuccessUrl("/login?logout")    // 登出成功後，回到登入頁並在網址帶上 ?logout 參數
                .invalidateHttpSession(true)          // 徹底清除伺服器端的 Session
                .deleteCookies("JSESSIONID")          // 刪除瀏覽器上的憑證 Cookie
                .permitAll()
            );

        return http.build();
    }
}