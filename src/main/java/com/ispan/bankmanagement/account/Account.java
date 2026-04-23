package com.ispan.bankmanagement.account;

import com.ispan.bankmanagement.account.enums.AccountStatus;
import com.ispan.bankmanagement.account.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "account")

// 不用Data override equals() 和 hashCode()
@Getter
@Setter

@NoArgsConstructor
public class Account {

    @Id
    @Column(name = "account_number", length = 12, nullable = false)
    private String accountNumber;

    // nullable -> 不能為空
    @Column(name = "customer_id" , nullable = false)
    private Integer customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType type = AccountType.CURRENT;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.INACTIVE;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private OffsetDateTime createAt;

    @UpdateTimestamp
    @Column(name = "change_at", nullable = false)
    private OffsetDateTime changeAt;


    /**
     * 這裡的主要用意是，如果用原生的equals，他只會比記憶體位址。
     * 如果兩個裝著同樣客戶資料的物件，但他們的記憶體位址不一樣，就會直接false，
     * 所以為了符合業務邏輯，改寫equals，只比較"業務主鍵"的值 ( accountNumber )，
     * 例如：轉帳等等的業務，省去手寫一堆getAccountNumber...。
     * */
    @Override
    public boolean equals(Object o) {
        // memory address check
        // 同個地址不可能不一樣
        if (this == o) return true;

        // 防呆，如果型別不對就不用比了
        // instanceof 是子類也包含
        // hibernate 的代理物件也算是一種子類別
        if (!(o instanceof Account that)) return false;

        // 因為 JPA lazyload的關係 這邊不能寫這樣
        // 剛載入的時候 很可能還沒進SQL下指令撈資料
        // 導致 "代理物件" 是空的 用.accountNumber 會出事
        // return Objects.equals(accountNumber, that.accountNumber);
        return this.getAccountNumber() != null &&
                this.getAccountNumber().equals(that.getAccountNumber());
    }

    // 改了equals hashCode就要覆寫 同捆包
    @Override
    public int hashCode() {
        // 為了確保實體在不同生命週期（如剛 new 出來與存入 DB 後）的雜湊值穩定，
        // 在 JPA 實體中，最安全的做法是回傳固定值或基於 getClass() 計算。
        return getClass().hashCode();
    }
}
