package com.ispan.bankmanagement.common.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
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

    /**
     * 手動註冊 JdbcTemplate Bean
     * 確保 CustomerDao 能夠順利拿這把「萬能鑰匙」去連資料庫。
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 關閉 CSRF 防護 (開發階段先關閉，方便表單傳送)
            .csrf(csrf -> csrf.disable())
            
            // 2. 設定網址的存取權限
            .authorizeHttpRequests(auth -> auth
                // ✨ 關鍵修復：除了放行 /login，也要確保所有相關資源開放通行
                // 包含妳的 AuthController 導向的頁面與靜態檔案
                .requestMatchers("/login", "/customer-login", "/error", "/css/**", "/js/**", "/images/**").permitAll() 
                
                // 其他所有的網址 (例如 /customer) 都必須經過登入驗證
                .anyRequest().authenticated()
            )
            
            // 3. 設定表單登入機制
            .formLogin(form -> form
                .loginPage("/login")                  // 告訴系統：我們自訂的登入畫面網址
                .defaultSuccessUrl("/customer", true) // 登入成功後，強制導向「顧客清單」頁面
                .permitAll()                          // 允許所有人執行登入動作
            )
            
            // 4. 設定登出機制
            .logout(logout -> logout
                .logoutUrl("/logout")                 // 觸發登出的網址
                .logoutSuccessUrl("/login?logout")    // 登出成功後回到登入頁
                .invalidateHttpSession(true)          // 徹底清除 Session
                .deleteCookies("JSESSIONID")          // 刪除憑證 Cookie
                .permitAll()
            );

        return http.build();
    }
}