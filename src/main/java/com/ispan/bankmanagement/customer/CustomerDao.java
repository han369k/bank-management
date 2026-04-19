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
public class CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    // 新增顧客
    public boolean insertCustomer(CustomerVo c) {
        // 配合 DB 底線命名
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

    // 查詢全部
    public List<CustomerVo> findAll() {
        // 用 AS 將底線轉駝峰
        String sql = "SELECT customer_id AS customerId, id_number AS idNumber, name, date_of_birth AS dateOfBirth, "
                   + "address, phone, email, income, credit_score AS creditScore, status "
                   + "FROM CUSTOMER";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CustomerVo.class));
    }

    // 依身分證查詢
    public CustomerVo findByIdNumber(String idNumber) {
        // 用 AS 轉駝峰
        String sql = "SELECT customer_id AS customerId, id_number AS idNumber, name, date_of_birth AS dateOfBirth, "
                   + "address, phone, email, income, credit_score AS creditScore, status "
                   + "FROM CUSTOMER WHERE id_number = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(CustomerVo.class), idNumber);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 依 ID 查詢
    public CustomerVo findById(Integer customerId) {
        String sql = "SELECT customer_id AS customerId, id_number AS idNumber, name, date_of_birth AS dateOfBirth, "
                   + "address, phone, email, income, credit_score AS creditScore, status "
                   + "FROM CUSTOMER WHERE customer_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(CustomerVo.class), customerId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 更新狀態
    public boolean updateStatus(Integer customerId, String newStatus) {
        String sql = "UPDATE CUSTOMER SET status = ? WHERE customer_id = ?";

        int rows = jdbcTemplate.update(sql, newStatus, customerId);
        return rows > 0;
    }

    // 刪除
    public boolean deleteCustomer(Integer customerId) {
        String sql = "DELETE FROM CUSTOMER WHERE customer_id = ?";

        int rows = jdbcTemplate.update(sql, customerId);
        return rows > 0;
    }
}