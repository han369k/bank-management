package com.ispan.bankmanagement.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trans_log")
@Getter
@Setter
@NoArgsConstructor
public class TransLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_log_id")
    private Integer transLogId;

    @Column(name = "reference_id", length = 50)
    private String referenceId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "operation_account", length = 12, nullable = false)
    private String operationAccount;

    @Column(name = "other_account", length = 12)
    private String otherAccount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @CreationTimestamp
    @Column(name = "transaction_time", nullable = false, updatable = false)
    private OffsetDateTime transactionTime;

    @Column(length = 255)
    private String note;

    /**
     * 對於流水號主鍵 (Identity)，equals 的實作邏輯：
     * 如果 ID 相同，則視為同一筆交易紀錄。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // 透過 instanceof 處理 Hibernate Proxy 代理物件
        if (!(o instanceof TransLog that)) return false;

        // 由於 ID 是由資料庫生成的，必須確保 ID 不為 null 才能判定相等
        return this.getTransLogId() != null &&
                this.getTransLogId().equals(that.getTransLogId());
    }

    @Override
    public int hashCode() {
        // 維持實體同一性最穩定的作法
        return getClass().hashCode();
    }
}