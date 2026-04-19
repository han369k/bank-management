package com.ispan.bankmanagement.loan;

import com.ispan.bankmanagement.loan.dto.LoanApplicationRequestDTO;
import com.ispan.bankmanagement.loan.dto.LoanApplicationResponseDTO;
import com.ispan.bankmanagement.loan.dto.LoanRejectRequestDTO;
import com.ispan.bankmanagement.loan.dto.LoanReviewRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loan")
@Validated
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanService;

    // ===============================
    // 📌 查詢
    // ===============================

    // 🔹 查全部申請（GET /api/loan）
    @GetMapping
    public ResponseEntity<List<LoanApplicationResponseDTO>> getAll() {

        List<LoanApplicationResponseDTO> list = loanService.getAll();
        return ResponseEntity.ok(list);
    }

    // 🔹 依狀態查詢（GET /api/loan/search?status=PENDING&status=APPROVED）
    @GetMapping("/search")
    public ResponseEntity<List<LoanApplicationResponseDTO>> getByStatus(
            @RequestParam List<LoanApplicationStatus> status) {

        List<LoanApplicationResponseDTO> list = loanService.getByStatus(status);
        return ResponseEntity.ok(list);
    }

    // 🔹 利率試算（GET /api/loan/rate?applyType=CAR&term=36）
    @GetMapping("/rate")
    public ResponseEntity<Map<String, Object>> calculateRate(
            @RequestParam String applyType,
            @RequestParam Integer term) {

        BigDecimal rate = loanService.calculateRate(applyType, term);
        return ResponseEntity.ok(Map.of("rate", rate));
    }

    // ===============================
    // 📌 客戶操作
    // ===============================

    // 🔹 新增貸款申請（POST /api/loan）
    @PostMapping
    public ResponseEntity<Map<String, String>> insert(
            @RequestBody @Valid LoanApplicationRequestDTO dto) {

        String applicationId = loanService.insert(dto);
        return ResponseEntity.ok(Map.of(
                "message", "申請成功",
                "applicationId", applicationId
        ));
    }

    // 🔹 客戶確認核准方案（PUT /api/loan/{id}/confirm）
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, String>> confirmApproval(
            @PathVariable("id") String applicationId) {

        loanService.confirmApproval(applicationId);
        return ResponseEntity.ok(Map.of("message", "客戶已確認核准"));
    }

    // 🔹 客戶拒絕銀行方案（PUT /api/loan/{id}/reject-by-customer）
    // ✅ 不需 @RequestBody，applicationId 從 PathVariable 取即可
    @PutMapping("/{id}/reject-by-customer")
    public ResponseEntity<Map<String, String>> rejectByCustomer(
            @PathVariable("id") String applicationId) {

        loanService.rejectByCustomer(applicationId);
        return ResponseEntity.ok(Map.of("message", "客戶已拒絕方案"));
    }

    // ===============================
    // 📌 銀行（後台）操作
    // ===============================

    // 🔹 銀行送方案給客戶確認（PUT /api/loan/{id}/submit-confirm）
    @PutMapping("/{id}/submit-confirm")
    public ResponseEntity<Map<String, String>> submitForConfirm(
            @PathVariable("id") String applicationId,
            @RequestBody @Valid LoanReviewRequestDTO dto) {

        loanService.submitForConfirm(applicationId, dto);
        return ResponseEntity.ok(Map.of("message", "已送出方案，待客戶確認"));
    }

    // 🔹 銀行直接核准（PUT /api/loan/{id}/approve-direct）
    @PutMapping("/{id}/approve-direct")
    public ResponseEntity<Map<String, String>> approveDirectly(
            @PathVariable("id") String applicationId,
            @RequestBody @Valid LoanReviewRequestDTO dto) {

        loanService.approveDirectly(applicationId, dto);
        return ResponseEntity.ok(Map.of("message", "已直接核准"));
    }

    // 🔹 銀行拒絕（PUT /api/loan/{id}/reject-by-bank）
    // ✅ 原本 Map<String, Integer> 改為具名 DTO
    @PutMapping("/{id}/reject-by-bank")
    public ResponseEntity<Map<String, String>> rejectByBank(
            @PathVariable("id") String applicationId,
            @RequestBody @Valid LoanRejectRequestDTO dto) {

        loanService.rejectByBank(applicationId, dto);
        return ResponseEntity.ok(Map.of("message", "銀行已拒絕申請"));
    }
}