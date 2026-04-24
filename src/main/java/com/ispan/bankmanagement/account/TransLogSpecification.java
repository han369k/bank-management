package com.ispan.bankmanagement.account;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransLogSpecification {

    /**
     * 實作複雜的多條件動態拼接，等同於以前寫的一大堆 if (xxx != null) { sql += " AND xxx = ?" }
     */
    public static Specification<TransLog> dynamicQuery(Integer customerId, String account, LocalDate startDate, LocalDate endDate) {
        
        // root: 代表 FROM TransLog (我們要查的主表)
        // query: 用來建構子查詢或其他結構
        // cb (CriteriaBuilder): 負責產生條件運算子 (=, IN, >=, <)
        return (root, query, cb) -> {
            
            // 準備一個「籃子」來裝所有的條件，這就等於 WHERE 1=1
            List<Predicate> predicates = new ArrayList<>();

            // 1. 如果有傳入特定帳號
            if (StringUtils.hasText(account)) {
                // 等同於：AND operation_account = ?
                predicates.add(cb.equal(root.get("operationAccount"), account));
            }

            // 2. 如果有傳入客戶 ID，這段稍微複雜一點，等同於 Repository 寫的 JPQL 子查詢
            if (customerId != null) {
                // 建立子查詢：SELECT a.accountNumber FROM Account a WHERE a.customerId = ?
                Subquery<String> subquery = query.subquery(String.class);
                Root<Account> accountRoot = subquery.from(Account.class);
                subquery.select(accountRoot.get("accountNumber"))
                        .where(cb.equal(accountRoot.get("customerId"), customerId));
                
                // 把子查詢塞進 IN 條件：AND operation_account IN (子查詢)
                predicates.add(root.get("operationAccount").in(subquery));
            }

            // 3. 查詢區間 (開始時間)
            if (startDate != null) {
                // 等同於：AND transaction_time >= '2026-01-01 00:00:00'
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionTime"), startDate.atStartOfDay()));
            }
            
            // 4. 查詢區間 (結束時間)
            if (endDate != null) {
                // 為了包含結束日當天的整天，所以加 1 天然後用「小於」。
                // 舉例：要查到 1/31 23:59:59，邏輯等同於 < '2026-02-01 00:00:00'
                predicates.add(cb.lessThan(root.get("transactionTime"), endDate.plusDays(1).atStartOfDay()));
            }

            // 最後一步：把籃子裡所有的條件，全部用 AND 串起來交給 JPA 執行
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}