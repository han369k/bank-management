package com.ispan.bankmanagement.loan;

import com.ispan.bankmanagement.loan.dto.LoanApplicationRequestDTO;
import com.ispan.bankmanagement.loan.dto.LoanApplicationResponseDTO;
import com.ispan.bankmanagement.loan.dto.LoanRejectRequestDTO;
import com.ispan.bankmanagement.loan.dto.LoanReviewRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanApplicationService {

    @Autowired
    private LoanApplicationRepository LARepo;

    // ===============================
    // 📌 查詢
    // ===============================

    // 🔹 1. 查全部申請
    public List<LoanApplicationResponseDTO> getAll() {
        return LARepo.findAllByOrderByCreateTimeDesc()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 🔹 2. 依據 status 查詢
    public List<LoanApplicationResponseDTO> getByStatus(List<LoanApplicationStatus> statusList) {
        return LARepo.findByStatusInOrderByCreateTimeDesc(statusList)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // 📌 客戶操作
    // ===============================

    // 🔹 3. 新增申請 — 改接 RequestDTO，回傳 applicationId
    public String insert(LoanApplicationRequestDTO dto) {

        LoanApplication loan = new LoanApplication();

        // 自動產生 applicationId：LA + yyyyMMddHHmmss + 4碼隨機數
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%04d", (int)(Math.random() * 10000));
        loan.setApplicationId("LA" + timeStr + randomSuffix);

        // 從 DTO 填入客戶申請資料
        loan.setCustomerId(dto.getCustomerId());
        loan.setApplyType(dto.getApplyType());
        loan.setApplyAmount(dto.getApplyAmount());
        loan.setApplyPeriod(dto.getApplyPeriod());

        // 後端統一計算利率
        BigDecimal rate = calculateRate(dto.getApplyType(), dto.getApplyPeriod());
        loan.setRate(rate);

        // 後端預設欄位
        loan.setStatus(LoanApplicationStatus.PENDING);
        loan.setCreateTime(LocalDateTime.now());

        LARepo.save(loan);

        return loan.getApplicationId();
    }

    // 🔹 4. 客戶確認核准方案（無需 DTO，PathVariable 即可）
    public void confirmApproval(String applicationId) {

        LoanApplication existing = LARepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("找不到申請編號：" + applicationId));

        existing.setReviewTime(LocalDateTime.now());
        existing.setStatus(LoanApplicationStatus.CONFIRMED);

        LARepo.save(existing);
    }

    // 🔹 5. 客戶拒絕銀行方案（無需 DTO，PathVariable 即可）
    public void rejectByCustomer(String applicationId) {

        LoanApplication existing = LARepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("找不到申請編號：" + applicationId));

        existing.setReviewTime(LocalDateTime.now());
        existing.setStatus(LoanApplicationStatus.REJECTED);

        LARepo.save(existing);
    }

    // ===============================
    // 📌 銀行（後台）操作
    // ===============================

    // 🔹 6. 銀行送方案給客戶確認 — 改接 applicationId + ReviewDTO
    public void submitForConfirm(String applicationId, LoanReviewRequestDTO dto) {

        LoanApplication existing = LARepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("找不到申請編號：" + applicationId));

        // applyType 從 DB 取，不信任前端傳入
        BigDecimal rate = calculateRate(existing.getApplyType(), dto.getApprovedPeriod());

        existing.setApprovedAmount(dto.getApprovedAmount());
        existing.setApprovedPeriod(dto.getApprovedPeriod());
        existing.setApprovedRate(rate);                     // 後端計算覆蓋
        existing.setReviewerId(dto.getReviewerId());
        existing.setReviewTime(LocalDateTime.now());
        existing.setStatus(LoanApplicationStatus.PENDING_CONFIRM);

        LARepo.save(existing);
    }

    // 🔹 7. 銀行直接核准 — 改接 applicationId + ReviewDTO
    public void approveDirectly(String applicationId, LoanReviewRequestDTO dto) {

        LoanApplication existing = LARepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("找不到申請編號：" + applicationId));

        // applyType 從 DB 取，不信任前端傳入
        BigDecimal rate = calculateRate(existing.getApplyType(), dto.getApprovedPeriod());

        existing.setApprovedAmount(dto.getApprovedAmount());
        existing.setApprovedPeriod(dto.getApprovedPeriod());
        existing.setApprovedRate(rate);                     // 後端計算覆蓋
        existing.setReviewerId(dto.getReviewerId());
        existing.setReviewTime(LocalDateTime.now());
        existing.setStatus(LoanApplicationStatus.APPROVED);

        LARepo.save(existing);
    }

    // 🔹 8. 銀行拒絕 — 改接 applicationId + RejectDTO
    public void rejectByBank(String applicationId, LoanRejectRequestDTO dto) {

        LoanApplication existing = LARepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("找不到申請編號：" + applicationId));

        existing.setReviewerId(dto.getReviewerId());
        existing.setReviewTime(LocalDateTime.now());
        existing.setStatus(LoanApplicationStatus.REJECTED);

        LARepo.save(existing);
    }

    // ===============================
    // 📌 內部工具方法
    // ===============================

    // 🔹 9. 利率計算（邏輯不變）
    public BigDecimal calculateRate(String applyType, Integer term) {

        if (applyType == null || term == null) {
            return new BigDecimal("0.03");
        }

        BigDecimal baseRate;
        switch (applyType) {
            case "PERSONAL":
                baseRate = new BigDecimal("0.04");
                validateTerm(term, new int[]{12, 24, 36, 48, 60});
                break;
            case "CAR":
                baseRate = new BigDecimal("0.025");
                validateTerm(term, new int[]{12, 24, 36, 48, 60});
                break;
            case "MOTOR":
                baseRate = new BigDecimal("0.045");
                validateTerm(term, new int[]{12, 24, 36});
                break;
            case "STUDENT":
                validateTerm(term, new int[]{60, 84, 120});
                return new BigDecimal("0.015");
            case "BUSINESS":
                baseRate = new BigDecimal("0.02");
                validateTerm(term, new int[]{36, 60, 84});
                break;
            case "HOUSE":
                baseRate = new BigDecimal("0.018");
                validateTerm(term, new int[]{120, 240, 360, 480});
                break;
            case "LAND":
                baseRate = new BigDecimal("0.028");
                validateTerm(term, new int[]{120, 180, 240});
                break;
            default:
                baseRate = new BigDecimal("0.03");
        }

        BigDecimal termRate;
        switch (term) {
            case 12:  termRate = BigDecimal.ZERO; break;
            case 24:  termRate = new BigDecimal("0.002"); break;
            case 36:  termRate = new BigDecimal("0.005"); break;
            case 48:  termRate = new BigDecimal("0.008"); break;
            case 60:  termRate = new BigDecimal("0.01");  break;
            case 84:  termRate = new BigDecimal("0.015"); break;
            case 120: termRate = BigDecimal.ZERO; break;
            case 180: termRate = new BigDecimal("0.002"); break;
            case 240: termRate = new BigDecimal("0.004"); break;
            case 360: termRate = new BigDecimal("0.006"); break;
            case 480: termRate = new BigDecimal("0.008"); break;
            default:  termRate = BigDecimal.ZERO;
        }

        return baseRate.add(termRate);
    }

    // 🔹 期數驗證（邏輯不變）
    private void validateTerm(int term, int[] allowed) {

        for (int t : allowed) {
            if (t == term) return;
        }
        throw new RuntimeException("此貸款種類不支援該期數：" + term);
    }

    // 🔹 Entity → ResponseDTO 轉換
    private LoanApplicationResponseDTO toResponseDTO(LoanApplication loan) {

        LoanApplicationResponseDTO dto = new LoanApplicationResponseDTO();

        dto.setApplicationId(loan.getApplicationId());
        dto.setCustomerId(loan.getCustomerId());
        dto.setApplyType(loan.getApplyType());
        dto.setApplyAmount(loan.getApplyAmount());
        dto.setApplyPeriod(loan.getApplyPeriod());
        dto.setRate(loan.getRate());
        dto.setStatus(loan.getStatus());
        dto.setApprovedAmount(loan.getApprovedAmount());
        dto.setApprovedPeriod(loan.getApprovedPeriod());
        dto.setApprovedRate(loan.getApprovedRate());
        dto.setReviewerId(loan.getReviewerId());
        dto.setCreateTime(loan.getCreateTime());
        dto.setReviewTime(loan.getReviewTime());

        return dto;
    }
}