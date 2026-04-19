package com.ispan.bankmanagement.account;

import com.ispan.bankmanagement.account.dto.TransLogDetailResponse;
import com.ispan.bankmanagement.account.dto.TransLogQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // 等同於 @Controller + 所有方法自動加上 @ResponseBody，會自動把回傳的 Java 物件轉成 JSON
@RequestMapping("/api/translogs") // 統一 API 路由前綴，符合 RESTful 命名規範
@RequiredArgsConstructor // Lombok 會自動針對標註 final 的欄位產生 Constructor 進行 Dependency Injection
public class TransLogController {

    private final TransLogService transLogService;

    // ==========================================
    // GET 請求：查詢操作
    // ==========================================

    /**
     * 透過 Reference ID 查詢單筆交易
     * URL: GET /api/translogs/reference/{referenceId}
     */
    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<TransLogDetailResponse> getByReferenceId(@PathVariable String referenceId) {
        // 將查詢結果包裝在 ResponseEntity.ok() 裡面，回傳標準的 HTTP Status 200 OK
        return ResponseEntity.ok(transLogService.getLogByReferenceId(referenceId));
    }

    /**
     * 查詢特定帳號的所有交易紀錄
     * URL: GET /api/translogs/account/{account}
     */
    @GetMapping("/account/{account}")
    public ResponseEntity<List<TransLogDetailResponse>> getByAccount(@PathVariable String account) {
        return ResponseEntity.ok(transLogService.getLogsByAccount(account));
    }

    /**
     * 查詢特定客戶名下所有帳戶的交易紀錄
     * URL: GET /api/translogs/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransLogDetailResponse>> getByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(transLogService.getLogsByCustomerId(customerId));
    }

    /**
     * 動態條件查詢交易紀錄 (支援分頁，並一併返回總筆數、總頁數)
     * 範例: GET /api/translogs?customerId=1&account=808...&startDate=2026-01-01&page=1
     * 
     * 這裡不使用多個 @RequestParam 接收，而是直接宣告 TransLogQueryRequest 這個 DTO 來接。
     * Spring MVC 會自動把 URL 網址後面的 ?xxx= 參數塞進 DTO 對應的屬性裡，讓 Controller 保持整潔。
     */
    @GetMapping
    public ResponseEntity<Page<TransLogDetailResponse>> searchLogs(TransLogQueryRequest request) {
        // 回傳的 Page 物件會自動被 Jackson 序列化成包含 content(資料陣列), totalElements(總筆數), totalPages(總頁數) 的完美 JSON 結構
        return ResponseEntity.ok(transLogService.searchLogs(request));
    }

    // ==========================================
    // DELETE 請求：資源變更操作
    // ==========================================

    /**
     * 刪除特定交易紀錄 (Demo 展示用途)
     * URL: DELETE /api/translogs/{referenceId}
     */
    @DeleteMapping("/{referenceId}")
    public ResponseEntity<Map<String, String>> deleteLog(@PathVariable String referenceId) {
        transLogService.deleteLogByReferenceId(referenceId);
        
        // 用 Map.of 快速組裝一個小 JSON 回應 {"message": "..."}，省去再建一個 Response DTO
        return ResponseEntity.ok(Map.of("message", "交易紀錄 " + referenceId + " 已刪除"));
    }
}