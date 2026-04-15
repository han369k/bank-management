package com.ispan.bankmanagement.account;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class TransLogSpecification {

    public static Specification<TransLog> dynamicSearch(Integer customerId, String account, LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 動態條件：帳號
            if (account != null && !account.isBlank()) {
                predicates.add(cb.equal(root.get("operationAccount"), account));
            }

            // 2. 動態條件：跨表查詢 CustomerId (對應舊 DAO 的 JOIN 寫法)
            if (customerId != null) {
                // Criteria API 的子查詢寫法：SELECT account_number FROM account WHERE customer_id = ?
                Subquery<String> subquery = query.subquery(String.class);
                Root<Account> accountRoot = subquery.from(Account.class);
                subquery.select(accountRoot.get("accountNumber"))
                        .where(cb.equal(accountRoot.get("customerId"), customerId));

                // 主查詢條件：operationAccount IN (子查詢的結果)
                predicates.add(root.get("operationAccount").in(subquery));
            }

            // 3. 動態條件：日期區間 (大於等於 startDate，小於 endDate + 1天)
            // 注意 TransLog 實體時間型別是 OffsetDateTime，所以這裡要幫 LocalDate 轉型對齊
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionTime"),
                        startDate.atStartOfDay().atOffset(ZoneOffset.UTC)));
            }
            if (endDate != null) {
                predicates.add(cb.lessThan(root.get("transactionTime"),
                        endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}