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

    public static Specification<TransLog> dynamicQuery(Integer customerId, String account, LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(account)) {
                predicates.add(cb.equal(root.get("operationAccount"), account));
            }

            if (customerId != null) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<Account> accountRoot = subquery.from(Account.class);
                subquery.select(accountRoot.get("accountNumber"))
                        .where(cb.equal(accountRoot.get("customerId"), customerId));
                predicates.add(root.get("operationAccount").in(subquery));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionTime"), startDate.atStartOfDay()));
            }
            if (endDate != null) {
                predicates.add(cb.lessThan(root.get("transactionTime"), endDate.plusDays(1).atStartOfDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}