package com.ispan.bankmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;


@SpringBootApplication
public class BankmanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankmanagementApplication.class, args);
    }

}
