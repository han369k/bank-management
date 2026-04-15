package com.ispan.bankmanagement.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransLogRepository extends JpaRepository<TransLog, Integer>, JpaSpecificationExecutor<TransLog> {

    // 1. 取代舊的 findByReferenceId
    Optional<TransLog> findByReferenceId(String referenceId);

    // 2. 取代舊的 deleteByReferenceId
    void deleteByReferenceId(String referenceId);

    // 3. 取代舊的 findByOperationAccount (自動按時間倒序)
    List<TransLog> findByOperationAccountOrderByTransactionTimeDesc(String operationAccount);

    /**
     * 4. 取代舊的 findByCustomerId
     * 舊 DAO 使用 JOIN account 表。
     * 在 JPA 中，因為我們 TransLog 裡只有 operationAccount 字串，沒有直接關聯 Account 物件，
     * 所以最乾淨的做法是利用 "子查詢 (Subquery)" 來達成跨表撈取。
     */
    @Query("SELECT t FROM TransLog t WHERE t.operationAccount IN " +
            "(SELECT a.accountNumber FROM Account a WHERE a.customerId = :customerId) " +
            "ORDER BY t.transactionTime DESC")
    List<TransLog> findByCustomerId(@Param("customerId") Integer customerId);
}