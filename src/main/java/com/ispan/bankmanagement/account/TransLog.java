package com.ispan.bankmanagement.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.ispan.bankmanagement.account.enums.TransLogType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trans_log")
// 實體類別一律不推薦用 @Data，避免 lombok 自動產生的 equals/hashCode 觸發 Lazy Loading 導致效能問題或 N+1
@Getter
@Setter
@NoArgsConstructor
public class TransLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_log_id")
    private Integer transLogId; // 流水號主鍵，交給資料庫自動遞增

    // UUID 生成的參考碼，用來把「轉出」跟「轉入」這兩筆原本獨立的 Log 綁在一起看
    @Column(name = "reference_id", length = 50)
    private String referenceId;

    // 金額相關的一律用 BigDecimal，精確度設為 19，小數點 4 位，避免浮點數運算誤差
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    // 告訴 JPA 存入資料庫時直接存字串 (例如 "DEPOSIT")，不要存 Enum 的 index 數字
    // 不然以後如果在 Enum 之間安插了新狀態，資料庫裡的數字對應就全毀了
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransLogType type;

    // 發起這筆交易的主帳號
    @Column(name = "operation_account", length = 12, nullable = false)
    private String operationAccount;

    // 交易的對手帳號 (存款/提款因為沒有對手，所以這個欄位可以為 null)
    @Column(name = "other_account", length = 12)
    private String otherAccount;

    // 交易"後"的餘額，實務上這個很重要，未來對帳或是查帳時可以核對結餘
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    // updatable = false 確保交易時間建立後，絕對不允許透過 JPA 程式層面再去被修改
    @CreationTimestamp
    @Column(name = "transaction_time", nullable = false, updatable = false)
    private LocalDateTime transactionTime;

    // columnDefinition 指定 DB 使用 NVARCHAR，確保存入中文備註不會變成亂碼
    @Column(columnDefinition = "NVARCHAR(255)")
    private String note;

    /**
     * 對於流水號主鍵 (Identity)，equals 的實作邏輯：
     * 如果 ID 相同，則視為同一筆交易紀錄。
     */
    @Override
    public boolean equals(Object o) {
        // memory address check
        if (this == o) return true;
        
        // 防呆，透過 instanceof 處理 Hibernate Proxy 代理物件
        // 當 JPA 採用 Lazy loading 撈資料時，物件會是被 CGLIB 增強的子類別
        if (!(o instanceof TransLog that)) return false;

        // 由於 ID 是由資料庫生成的，實體剛 new 出來還沒 save 前 ID 會是 null
        // 必須確保 ID 不為 null 才能判定相等，否則兩個剛 new 出來的物件會被誤判為同一個
        return this.getTransLogId() != null &&
                this.getTransLogId().equals(that.getTransLogId());
    }

    @Override
    public int hashCode() {
        // 為了確保實體在不同生命週期（如剛 new 出來與存入 DB 後）的雜湊值穩定
        // 維持實體同一性最安全的作法是基於 getClass() 回傳固定值
        return getClass().hashCode();
    }
}