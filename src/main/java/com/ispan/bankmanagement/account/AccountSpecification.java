package com.ispan.bankmanagement.account;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AccountSpecification {

    /**
     * 這裡就等同於以前寫的 query(Connection conn, Account accountEntity)
     */
    public static Specification<Account> dynamicQuery(String type, String status, String keyword) {

        // root: 代表 FROM Account (你的實體類別)
        // query: 代表 SELECT (通常用不到，除非你要寫 GROUP BY)
        // cb (CriteriaBuilder): 負責產生條件運算子 (=, LIKE, >, <)
        return (root, query, cb) -> {

            // 準備一個「籃子」，這就等於 WHERE 1=1
            List<Predicate> predicates = new ArrayList<>();

            // 1. 如果有傳入 type，就加一個「等於」的條件
            if (type != null && !type.isBlank()) {
                // 等同於：AND type = ?
                predicates.add(cb.equal(root.get("type"), type));
            }

            // 2. 如果有傳入 status，也加進去
            if (status != null && !status.isBlank()) {
                // 等同於：AND status = ?
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 3. 模糊查詢，如果使用者有輸入帳號關鍵字
            if (keyword != null && !keyword.isBlank()) {
                // 等同於：AND account_number LIKE '%?%'
                predicates.add(cb.like(root.get("accountNumber"), "%" + keyword + "%"));
            }

            // 最後一步：把籃子裡所有的條件，全部用 AND 串起來交給 JPA 執行
            // 如果 predicates 是空的，JPA 就不會產生 WHERE 子句 (直接撈全部)
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}