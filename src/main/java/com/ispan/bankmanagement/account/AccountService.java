package com.ispan.bankmanagement.account;

import com.ispan.bankmanagement.account.enums.AccountStatus;
import com.ispan.bankmanagement.account.dto.*;
import com.ispan.bankmanagement.account.enums.AccountCurrency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j // Lombok 自動產生 logger
@Service
@RequiredArgsConstructor // 自動注入帶有 final 的 Repository
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransLogRepository transLogRepository;

    // todo:
    //  1. 前端要移除新增帳號的input部分
    //  2. 重新安排mock的資料格式
    /**
     * 新增帳戶
     */
    public void createAcc(AccountCreateRequest request) {
        if (request == null) {
            log.warn("新增失敗，請求資料為空。");
            throw new IllegalArgumentException("帳戶資料不可為空。");
        }

        Account accountEntity = new Account();
        accountEntity.setCustomerId(request.customerId());
        if (request.accountType() != null) {
            accountEntity.setType(request.accountType());
        }
        if (request.accountCurrency() != null) {
            accountEntity.setCurrency(request.accountCurrency().name());
        }
        accountEntity.setBalance(request.balance() != null ? request.balance() : BigDecimal.ZERO);
        accountEntity.setStatus(AccountStatus.INACTIVE);

        String newAccountNumber;

        // 2. 【核心邏輯】：不斷產生新帳號，直到資料庫裡面找不到為止 (防撞號機制)
        do {
            newAccountNumber = generateRandomAccountNumber();
        } while (accountRepository.existsById(newAccountNumber));

        // 3. 將確認無重複的帳號塞入實體中
        accountEntity.setAccountNumber(newAccountNumber);

        // 5. 儲存進資料庫
        accountRepository.save(accountEntity);
        log.info("帳戶 {} 建立成功。", accountEntity.getAccountNumber());
    }

    /**
     * 私有輔助方法：產生 808 開頭的 12 碼隨機帳號
     */
    private String generateRandomAccountNumber() {
        // 銀行代碼 (3碼)
        String bankCode = "808";

        // 產生 0 ~ 999999999 之間的隨機數 (9碼)
        // 使用 ThreadLocalRandom 效能比 Math.random() 更好，適合高併發環境
        long randomNum = java.util.concurrent.ThreadLocalRandom.current().nextLong(1000000000L);

        // 將數字格式化為 9 碼字串，不足 9 碼會在前面自動補零 (例如 123 變成 000000123)
        String randomStr = String.format("%09d", randomNum);

        return bankCode + randomStr;
    }

    /**
     * 透過帳號查詢單筆帳戶資料 (內部實體使用)
     */
    private Account getAccountEntity(String accountNo) {
        // 使用 Optional 的 orElseThrow，一行解決查無資料拋例外的邏輯
        return accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("查無此帳戶，account: " + accountNo));
    }

    /**
     * 透過帳號查詢單筆帳戶資料 (回傳 DTO)
     */
    public AccountDetailResponse getAccountDetail(String accountNo) {
        Account account = getAccountEntity(accountNo);
        return convertToResponse(account);
    }

    /**
     * 查詢所有帳戶 (動態條件查詢)
     */
    public List<AccountDetailResponse> getAllAccounts(AccountQueryRequest request) {
        var spec = AccountSpecification.dynamicQuery(
                request.type(),
                request.status(),
                request.accountNumber()
        );
        List<Account> list = accountRepository.findAll(spec);
        log.info("帳戶條件查詢完成，共找到 {} 筆資料。", list.size());
        return list.stream().map(this::convertToResponse).toList();
    }

    /**
     * 更新帳戶狀態
     */
    @Transactional
    // 新增註解：這裡將 status 的型別從 String 改為 AccountStatus，確保從 Controller 傳進來的狀態絕對合法
    public void updateStatus(String accountNo, AccountStatus status) {
        // 先確認帳號存在 (防呆)
        if (!accountRepository.existsById(accountNo)) {
            throw new RuntimeException("更新狀態失敗，帳戶不存在: " + accountNo);
        }
        // 呼叫我們自訂的 @Modifying @Query
        accountRepository.updateStatus(accountNo, status);
        log.info("狀態更新成功, account: {}, 新狀態: {}", accountNo, status);
    }

    /**
     * 刪除帳戶 (Demo 用)
     */
    @Transactional
    public void deleteByAccount(String accountNo) {
        if (!accountRepository.existsById(accountNo)) {
            throw new RuntimeException("刪除失敗，帳戶不存在: " + accountNo);
        }
        accountRepository.deleteById(accountNo);
        log.warn("已成功刪除帳戶 account: {}", accountNo);
    }

    /**
     * 提款 (ACID 交易展示)
     */
    @Transactional
    public void withdraw(String accountNo, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("提款金額必須大於零");
        }

        // 1. 查詢帳戶
        Account account = getAccountEntity(accountNo);

        // 2. 業務邏輯防呆
        // 新增註解：將字串 equals 判斷改為 Enum 判斷，同時阻擋 FROZEN 與 CLOSED 狀態
        // 架構建議：由於 Enum 已經擴充至 10 種狀態，繼續使用黑名單阻擋會有漏洞 (例如 INACTIVE 也能提款)。
        // 建議未來改為白名單機制： if (account.getStatus() != AccountStatus.ACTIVE)
        if (account.getStatus() == AccountStatus.FROZEN || account.getStatus() == AccountStatus.CLOSED) {
            throw new RuntimeException("提款失敗，帳戶狀態異常: " + account.getStatus());
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("提款失敗，餘額不足");
        }

        // 3. 呼叫 @Modifying @Query 執行原子性扣款 (防高併發)
        int updateRows = accountRepository.updateBalance(accountNo, amount.negate());
        if (updateRows != 1) {
            throw new RuntimeException("扣款失敗，帳戶可能不存在");
        }

        // 4. 計算交易後餘額並寫入 Log
        BigDecimal balanceAfter = account.getBalance().subtract(amount);

        TransLog logEntity = new TransLog();
        logEntity.setReferenceId(UUID.randomUUID().toString());
        logEntity.setAmount(amount);
        logEntity.setType("WITHDRAW");
        logEntity.setOperationAccount(accountNo);
        logEntity.setBalance(balanceAfter);
        logEntity.setNote("提款");
        // transactionTime 由 @CreationTimestamp 在 Entity 內自動產生，無需手動 set

        transLogRepository.save(logEntity);
        log.info("提款成功！帳戶: {}, 提款金額: {}, 剩餘餘額: {}", accountNo, amount, balanceAfter);
    }

    /**
     * 存款
     */
    @Transactional
    public void deposit(String accountNo, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("存款金額必須大於零");
        }

        Account account = getAccountEntity(accountNo);

        // 新增註解：改用 Enum 判斷，凍結或銷戶皆不可存款
        // 架構建議：同上，建議改為白名單機制。
        if (account.getStatus() == AccountStatus.FROZEN || account.getStatus() == AccountStatus.CLOSED) {
            throw new RuntimeException("存款失敗，帳戶狀態異常: " + account.getStatus());
        }

        accountRepository.updateBalance(accountNo, amount);

        BigDecimal balanceAfter = account.getBalance().add(amount);

        TransLog logEntity = new TransLog();
        logEntity.setReferenceId(UUID.randomUUID().toString());
        logEntity.setAmount(amount);
        logEntity.setType("DEPOSIT");
        logEntity.setOperationAccount(accountNo);
        logEntity.setBalance(balanceAfter);
        logEntity.setNote("現金存款");

        transLogRepository.save(logEntity);
        log.info("存款成功！帳戶: {}, 金額: {}, 新餘額: {}", accountNo, amount, balanceAfter);
    }

    /**
     * 轉帳
     */
    @Transactional
    public void transfer(String fromAccNo, String toAccNo, BigDecimal amount, String note) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("轉帳金額必須大於零");
        }
        if (fromAccNo.equals(toAccNo)) {
            throw new IllegalArgumentException("轉出與轉入帳號不可相同");
        }

        // 1. 鎖定並檢查雙方帳戶狀態
        Account fromAcc = getAccountEntity(fromAccNo);
        if (fromAcc.getBalance().compareTo(amount) < 0) throw new RuntimeException("餘額不足");

        if ( fromAcc.getStatus() != AccountStatus.ACTIVE ) {
            throw new RuntimeException("轉出帳戶狀態異常: " + fromAcc.getStatus());
        }

        Account toAcc = getAccountEntity(toAccNo);

        if ( toAcc.getStatus() != AccountStatus.ACTIVE ) {
            throw new RuntimeException("轉入帳戶狀態異常: " + toAcc.getStatus());
        }

        // 2. 執行原子性餘額更新
        accountRepository.updateBalance(fromAccNo, amount.negate());
        accountRepository.updateBalance(toAccNo, amount);

        // 3. 準備日誌寫入
        BigDecimal fromBalanceAfter = fromAcc.getBalance().subtract(amount);
        BigDecimal toBalanceAfter = toAcc.getBalance().add(amount);
        String commonRef = UUID.randomUUID().toString();

        // 轉出紀錄
        TransLog fromLog = new TransLog();
        fromLog.setReferenceId(commonRef);
        fromLog.setAmount(amount.negate());
        fromLog.setType("TRANSFER_OUT");
        fromLog.setOperationAccount(fromAccNo);
        fromLog.setOtherAccount(toAccNo);
        fromLog.setBalance(fromBalanceAfter);
        fromLog.setNote(note != null ? note : "轉給 " + toAccNo);

        // 轉入紀錄
        TransLog toLog = new TransLog();
        toLog.setReferenceId(commonRef);
        toLog.setAmount(amount);
        toLog.setType("TRANSFER_IN");
        toLog.setOperationAccount(toAccNo);
        toLog.setOtherAccount(fromAccNo);
        toLog.setBalance(toBalanceAfter);
        toLog.setNote(note != null ? note : "來自 " + fromAccNo);

        transLogRepository.saveAll(List.of(fromLog, toLog)); // saveAll 可以一次存入多筆！

        log.info("轉帳成功！轉出帳戶: {} 轉入帳戶: {}，金額: {}", fromAccNo, toAccNo, amount);
    }

    /**
     * 輔助方法：將 Account 實體轉為 DetailResponse
     */
    private AccountDetailResponse convertToResponse(Account account) {
        return new AccountDetailResponse(
                account.getAccountNumber(),
                account.getCustomerId(),
                account.getType(),
                account.getCurrency() != null ? AccountCurrency.valueOf(account.getCurrency()) : null,
                account.getBalance(),
                account.getStatus(),
                account.getCreateAt(),
                account.getChangeAt()
        );
    }
}