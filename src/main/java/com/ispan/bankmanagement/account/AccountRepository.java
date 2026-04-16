package com.ispan.bankmanagement.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
// JpaSpecificationExecutor 實作動態拼接查詢條件
public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

    /**
     *  JPA預設的方法擋不到併發，所以要改用 @Query 自己下SQL ( 這是JPQL語法，欄位是Java的變數 )，
     *  只要牽扯到餘額的部分，計算一率不應該在 Java，要透過 DB 來運算。
     *  balance + amount 等於 Java 這邊完全不知道當前餘額，
     *  DB就必須先執行運算才能夠下去修改 ( 要熟悉SQL的執行順序，先運算後修改 )，
     *  修改時會把整個該row資料鎖住，修改結束（ 無論成功與否 ）才會解鎖，
     *  以此遵守ACID的原則，應對高併發。
     *  */
    // @Modifying -> 宣告這是一個會動到資料庫內容的方法
    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.accountNumber = :accountNumber")
    int updateBalance(@Param("accountNumber") String accountNumber, @Param("amount") BigDecimal amount);


    // 單純提高效能 不寫也沒差 省略SELECT 直接UPDATE
    @Modifying
    @Query("UPDATE Account a SET a.status = :status WHERE a.accountNumber = :accountNumber")
    int updateStatus(@Param("accountNumber") String accountNumber, @Param("status") AccountStatus status);
}