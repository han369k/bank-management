package com.ispan.bankmanagement.account;

import com.ispan.bankmanagement.account.dto.TransLogDetailResponse;
import com.ispan.bankmanagement.account.dto.TransLogQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/translogs")
@RequiredArgsConstructor
public class TransLogController {

    private final TransLogService transLogService;

    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<TransLogDetailResponse> getByReferenceId(@PathVariable String referenceId) {
        return ResponseEntity.ok(transLogService.getLogByReferenceId(referenceId));
    }

    @GetMapping("/account/{account}")
    public ResponseEntity<List<TransLogDetailResponse>> getByAccount(@PathVariable String account) {
        return ResponseEntity.ok(transLogService.getLogsByAccount(account));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransLogDetailResponse>> getByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(transLogService.getLogsByCustomerId(customerId));
    }

    /**
     * 動態條件查詢交易紀錄 (支援分頁，並一併返回總筆數、總頁數)
     * 範例: GET /api/translogs?customerId=1&account=808...&startDate=2026-01-01&page=1
     */
    @GetMapping
    public ResponseEntity<Page<TransLogDetailResponse>> searchLogs(TransLogQueryRequest request) {
        return ResponseEntity.ok(transLogService.searchLogs(request));
    }

    @DeleteMapping("/{referenceId}")
    public ResponseEntity<Map<String, String>> deleteLog(@PathVariable String referenceId) {
        transLogService.deleteLogByReferenceId(referenceId);
        return ResponseEntity.ok(Map.of("message", "交易紀錄 " + referenceId + " 已刪除"));
    }
}