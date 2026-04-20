package com.ispan.bankmanagement.customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 客戶服務邏輯
 */
@Slf4j
@Service 
@RequiredArgsConstructor 
public class CustomerService {

    private final CustomerDao customerDaoSpr;

    /**
     * 列表
     */
    public List<CustomerVo> findAll() {
        return customerDaoSpr.findAll();
    }

    /**
     * 依身分證查詢
     */
    public CustomerVo findByIdNumber(String idNumber) {
        if (idNumber == null || idNumber.isBlank()) {
            throw new IllegalArgumentException("身分證字號不可為空白");
        }
        return customerDaoSpr.findByIdNumber(idNumber.toUpperCase());
    }

    /**
     * 依 ID 查詢
     */
    public CustomerVo findById(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("顧客代號不可為空");
        }
        return customerDaoSpr.findById(customerId);
    }

    /**
     * 新增顧客
     */
    @Transactional 
    public boolean insertCustomer(CustomerCreateReq req) {
        
        // 檢查身分證是否重複
        if (customerDaoSpr.findByIdNumber(req.getIdNumber()) != null) {
            log.warn("新增失敗: 身分證 {} 已存在", req.getIdNumber());
            throw new IllegalArgumentException("身分證字號已存在");
        }

        CustomerVo vo = new CustomerVo();

        vo.setIdNumber(req.getIdNumber());
        vo.setName(req.getName());
        vo.setDateOfBirth(req.getDateOfBirth());
        vo.setNationality(req.getNationality());
        vo.setAddress(req.getAddress());
        vo.setPhone(req.getPhone());
        vo.setEmail(req.getEmail());
        
        vo.setIncome(req.getIncome() != null ? req.getIncome() : BigDecimal.ZERO);
        vo.setCreditScore(req.getCreditScore() != null ? req.getCreditScore() : 0);

        // 補齊系統欄位
        vo.setCustomerId(generateNextId());
        vo.setPasswordHash("default_hash_value");
        vo.setFailedLoginAttempts(0);
        vo.setStatus("Active");
        vo.setCreatedAt(LocalDateTime.now());
        vo.setUpdatedAt(LocalDateTime.now());

        boolean isInserted = customerDaoSpr.insertCustomer(vo);
        if (isInserted) {
            log.info("新增顧客成功, id: {}, name: {}", vo.getCustomerId(), vo.getName());
        }
        return isInserted;
    }

    /**
     * 更新狀態
     */
    @Transactional
    public boolean updateStatus(Integer customerId, String newStatus) {
        List<String> validStatuses = List.of("Active", "Inactive", "Suspended");
        if (!validStatuses.contains(newStatus)) {
            throw new IllegalArgumentException("無效的客戶狀態：" + newStatus);
        }

        boolean isUpdated = customerDaoSpr.updateStatus(customerId, newStatus);
        if (isUpdated) {
            log.info("顧客 {} 狀態更新為 {}", customerId, newStatus);
        }
        return isUpdated;
    }

    /**
     * 刪除
     */
    @Transactional
    public boolean deleteCustomer(Integer customerId) {
        // TODO: 刪除前需確認帳戶餘額是否結清
        boolean isDeleted = customerDaoSpr.deleteCustomer(customerId);
        if (isDeleted) {
            log.warn("顧客 {} 已被刪除", customerId);
        }
        return isDeleted;
    }

    /**
     * 產生 8 碼隨機 ID
     */
    private Integer generateNextId() {
        return 10000000 + new Random().nextInt(90000000); 
    }
}