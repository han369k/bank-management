package com.ispan.bankmanagement.customer;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service 層 — Java Bank 的業務邏輯處理中心
 * 負責處理商業邏輯與資料檢核，並負責 DTO 與 VO 之間的轉換。
 */
@Service 
@RequiredArgsConstructor 
public class CustomerService {

    // 注入升級版的 Spring Dao
    private final CustomerDao customerDaoSpr;

    /**
     * 查詢全部顧客
     */
    public List<CustomerVo> findAll() {
        // 回傳型別已經全面更新為 CustomerVo
        return customerDaoSpr.findAll();
    }

    /**
     * 用身分證查單一顧客
     */
    public CustomerVo findByIdNumber(String idNumber) {
        return customerDaoSpr.findByIdNumber(idNumber.toUpperCase());
    }

    /**
     * 新增顧客 (完美對接前端傳來的 DTO)
     */
    @Transactional 
    public boolean insertCustomer(CustomerCreateReq req) {
        
        // 1. 商業邏輯檢核：檢查身分證是否重複
        // 前端的 @Valid 已經幫我們擋掉空白格式了，這裡只需專心查資料庫邏輯
        if (customerDaoSpr.findByIdNumber(req.getIdNumber()) != null) {
            throw new IllegalArgumentException("此身分證字號已經註冊過囉！");
        }

        // 2. 準備一個要寫入 SQL Server 的資料庫實體 (VO)
        CustomerVo vo = new CustomerVo();

        // 3. 執行 DTO 轉 VO (Data Mapping)
        vo.setIdNumber(req.getIdNumber());
        vo.setName(req.getName());
        vo.setDateOfBirth(req.getDateOfBirth());
        vo.setNationality(req.getNationality());
        vo.setAddress(req.getAddress());
        vo.setPhone(req.getPhone());
        vo.setEmail(req.getEmail());
        
        // 處理金額與分數的預設值
        vo.setIncome(req.getIncome() != null ? req.getIncome() : BigDecimal.ZERO);
        vo.setCreditScore(req.getCreditScore() != null ? req.getCreditScore() : 0);

        // 4. 補齊系統自動產生的內部欄位
        vo.setCustomerId(generateNextId());                 // 自動產生顧客 ID
        vo.setPasswordHash("default_hash_value");           // 預設密碼 Hash
        vo.setFailedLoginAttempts(0);                       // 預設失敗次數
        vo.setStatus("Active");                             // 預設狀態為啟用

        // 5. 呼叫 DAO 寫入資料庫
        return customerDaoSpr.insertCustomer(vo);
    }

    /**
     * 修改顧客狀態
     */
    @Transactional
    public boolean updateStatus(String customerId, String newStatus) {
        List<String> validStatuses = List.of("Active", "Inactive", "Suspended");
        if (!validStatuses.contains(newStatus)) {
            throw new IllegalArgumentException("無效的客戶狀態：" + newStatus);
        }

        return customerDaoSpr.updateStatus(customerId, newStatus);
    }

    /**
     * 刪除顧客
     */
    @Transactional
    public boolean deleteCustomer(String customerId) {
        // 實務上在銀行系統，刪除前可能需要呼叫 AccountDao 檢查是否有餘額未結清
        return customerDaoSpr.deleteCustomer(customerId);
    }

    // ==================== 私有輔助方法 ====================

    /**
     * 模擬自動產生顧客代號 (例如：C + 當前時間戳)
     * 搭配 SQL Server 開發時，未來這段也可以考慮改用 NEWID() 來產生 UUID。
     */
    private String generateNextId() {
        return "C" + System.currentTimeMillis(); 
    }
}