package com.ispan.bankmanagement.account;

import com.ispan.bankmanagement.account.dto.*;
import com.ispan.bankmanagement.account.enums.AccountCurrency;
import com.ispan.bankmanagement.account.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/accounts/api") // 定義基礎路由
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // ==========================================
    // GET 請求：查詢操作
    // ==========================================

    /**
     * 查詢所有帳戶 (支援動態條件查詢)
     * URL: GET /api/accounts?type=SAVINGS&status=ACTIVE
     */
    @GetMapping
    public ResponseEntity<List<AccountDetailResponse>> getAllAccounts(AccountQueryRequest request) {
        return ResponseEntity.ok(accountService.getAllAccounts(request));
    }

    /**
     * 查詢單筆帳戶
     * URL: GET /api/accounts/{accountNo}
     */
    @GetMapping("/{accountNo}")
    public ResponseEntity<AccountDetailResponse> getAccount(@PathVariable String accountNo) {
        return ResponseEntity.ok(accountService.getAccountDetail(accountNo));
    }

    // ==========================================
    // POST / PATCH / DELETE 請求：資源變更操作
    // ==========================================

    /**
     * 新增帳戶
     * URL: POST /api/accounts
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createAccount(@RequestBody AccountCreateRequest request) {
        String newAccountNo = accountService.createAcc(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "帳戶建立成功", "accountNumber", newAccountNo));
    }

    /**
     * 更新帳戶狀態 (部分更新使用 PATCH)
     * URL: PATCH /api/accounts/{accountNo}/status
     */
    @PatchMapping("/{accountNo}/status")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable String accountNo,
            @RequestBody AccountUpdateRequest request) {

        if (request.accountStatus() == null) {
            throw new IllegalArgumentException("status 欄位為必填");
        }

        accountService.updateStatus(accountNo, request.accountStatus());
        return ResponseEntity.ok(Map.of("message", "狀態更新成功"));
    }

    /**
     * 刪除帳戶 (Demo 用)
     * URL: DELETE /api/accounts/{accountNo}
     */
    @DeleteMapping("/{accountNo}")
    public ResponseEntity<Map<String, String>> deleteAccount(@PathVariable String accountNo) {
        accountService.deleteByAccount(accountNo);
        return ResponseEntity.ok(Map.of("message", "帳戶刪除成功"));
    }

    // ==========================================
    // 商業邏輯操作：提款、存款、轉帳
    // ==========================================

    /**
     * 存款
     * URL: POST /api/accounts/{accountNo}/deposit
     */
    @PostMapping("/{accountNo}/deposit")
    public ResponseEntity<Map<String, String>> deposit(
            @PathVariable String accountNo,
            @RequestBody AccountAmountRequest request) {

        accountService.deposit(accountNo, request.amount());
        return ResponseEntity.ok(Map.of("message", "存款成功"));
    }

    /**
     * 提款
     * URL: POST /api/accounts/{accountNo}/withdraw
     */
    @PostMapping("/{accountNo}/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(
            @PathVariable String accountNo,
            @RequestBody AccountAmountRequest request) {

        accountService.withdraw(accountNo, request.amount());
        return ResponseEntity.ok(Map.of("message", "提款成功"));
    }

    /**
     * 轉帳
     * URL: POST /api/accounts/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody AccountTransferRequest request) {
        accountService.transfer(
                request.fromAccount(),
                request.toAccount(),
                request.amount(),
                request.note()
        );
        return ResponseEntity.ok(Map.of("message", "轉帳成功"));
    }
}