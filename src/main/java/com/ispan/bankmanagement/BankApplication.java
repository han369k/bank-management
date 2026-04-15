package com.ispan.bankmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // ✨ 這個註解非常重要，它是啟動 Spring 的關鍵
public class BankApplication {

    public static void main(String[] args) {
        // 這行就是所謂的 main type，是程式發動的引擎
        SpringApplication.run(BankApplication.class, args);
        System.out.println("🚀 銀行後台管理系統啟動成功！");
    }
}