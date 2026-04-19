package com.ispan.bankmanagement.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// JpaSpecificationExecutor 是為了讓這個 Repository 能吃 Specification 所產生的動態條件
public interface TransLogRepository extends JpaRepository<TransLog, Integer>, JpaSpecificationExecutor<TransLog> {

    // 1. 取代舊的 findByReferenceId 
    // 善用 Optional 包裝單筆結果，Service 層就不需要寫一堆 if (entity == null) 的判斷，
    // 直接用 .orElseThrow() 就能優雅拋出例外。
    Optional<TransLog> findByReferenceId(String referenceId);

    // 2. 取代舊的 deleteByReferenceId
    // 實務上銀行的 Log 絕對是 Insert-only，不會有 delete 方法，此處僅供專題展示用。
    void deleteByReferenceId(String referenceId);

    // 3. 取代舊的 findByOperationAccount 
    // 利用 Spring Data 命名規則 (OrderBy + 屬性 + Desc)，它會自動幫我們加上 ORDER BY 照時間倒序排
    List<TransLog> findByOperationAccountOrderByTransactionTimeDesc(String operationAccount);

    /**
     * 4. 取代舊的 findByCustomerId
     * 舊 DAO 使用 JOIN account 表。但在 JPA 中，因為我們為了降低實體間的耦合度，
     * TransLog 裡面只有 operationAccount (String)，並沒有宣告 @ManyToOne 關聯 Account 物件。
     * 所以這裡沒辦法直接用傳統 JPA 的關聯導航查詢。
     * 
     * 最乾淨的做法是直接下 JPQL 寫 "子查詢 (Subquery)"，先從 Account 表撈出該客戶所有的 accountNumber，
     * 再把它當作 IN (...) 的條件去篩選 TransLog。效能好，而且 Service 層不用寫迴圈分兩趟撈。
     */
    @Query("SELECT t FROM TransLog t WHERE t.operationAccount IN " +
            "(SELECT a.accountNumber FROM Account a WHERE a.customerId = :customerId) " +
            "ORDER BY t.transactionTime DESC")
    List<TransLog> findByCustomerId(@Param("customerId") Integer customerId);
}