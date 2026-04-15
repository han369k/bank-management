package com.ispan.bankmanagement.customer;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class CustomerDaoSpr {

    private final JdbcTemplate jdbcTemplate;

    // ==================== C：新增顧客 ====================
    public boolean insertCustomer(CustomerVoSpr c) {
        // ✨ 修正點 1：對齊資料庫真實的「底線命名」欄位名稱
        String sql = "INSERT INTO CUSTOMER "
                   + "(customer_id, id_number, name, date_of_birth, nationality, "
                   + " address, phone, email, password_hash, income, credit_score, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int rows = jdbcTemplate.update(sql,
                c.getCustomerId(),
                c.getIdNumber(),
                c.getName(),
                c.getDateOfBirth(),
                c.getNationality() != null ? c.getNationality() : "Taiwan",
                c.getAddress(),
                c.getPhone(),
                c.getEmail(),
                c.getPasswordHash(), 
                c.getIncome() != null ? c.getIncome() : BigDecimal.ZERO,
                c.getCreditScore(),
                c.getStatus() 
        );

        return rows > 0;
    }

    // ==================== R：查詢全部顧客 ====================
    public List<CustomerVoSpr> findAll() {
        // ✨ 修正點 2：用 AS 取別名，讓資料庫的底線欄位能完美對應 Java 的駝峰屬性！
        String sql = "SELECT customer_id AS customerId, id_number AS idNumber, name, date_of_birth AS dateOfBirth, "
                   + "address, phone, email, income, credit_score AS creditScore, status "
                   + "FROM CUSTOMER";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CustomerVoSpr.class));
    }

    // ==================== R：用身分證字號查詢單一顧客 ====================
    public CustomerVoSpr findByIdNumber(String idNumber) {
        // ✨ 修正點 3：一樣用 AS 取別名，並且 WHERE 條件要用資料庫真實的底線欄位
        String sql = "SELECT customer_id AS customerId, id_number AS idNumber, name, date_of_birth AS dateOfBirth, "
                   + "address, phone, email, income, credit_score AS creditScore, status "
                   + "FROM CUSTOMER WHERE id_number = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(CustomerVoSpr.class), idNumber);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // ==================== U：修改顧客狀態 ====================
    public boolean updateStatus(String customerId, String newStatus) {
        // ✨ 修正點 4：WHERE 條件改為 customer_id
        String sql = "UPDATE CUSTOMER SET status = ? WHERE customer_id = ?";

        int rows = jdbcTemplate.update(sql, newStatus, customerId);
        return rows > 0;
    }

    // ==================== D：刪除顧客 ====================
    public boolean deleteCustomer(String customerId) {
        // ✨ 修正點 5：WHERE 條件改為 customer_id
        String sql = "DELETE FROM CUSTOMER WHERE customer_id = ?";

        int rows = jdbcTemplate.update(sql, customerId);
        return rows > 0;
    }
}