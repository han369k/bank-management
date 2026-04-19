package com.ispan.bankmanagement.account;

import com.ispan.bankmanagement.account.dto.TransLogDetailResponse;
import com.ispan.bankmanagement.account.dto.TransLogQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok 自動產生 logger，省去宣告 private static final Logger ...
public class TransLogService {

    private final TransLogRepository transLogRepository;

    /**
     * 透過 UUID (Reference ID) 查詢單筆交易紀錄
     */
    public TransLogDetailResponse getLogByReferenceId(String referenceId) {
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("Reference ID 不可為空");
        }
        // 利用 Optional 的 orElseThrow，把原本的 null check 縮減成一行，同時自訂找不到時的例外訊息
        TransLog logEntity = transLogRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new RuntimeException("找不到該筆交易紀錄"));
        
        return convertToResponse(logEntity);
    }

    /**
     * 透過 Customer ID 查詢該名下所有帳戶的交易紀錄
     */
    public List<TransLogDetailResponse> getLogsByCustomerId(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID 不可為空");
        }
        List<TransLog> logs = transLogRepository.findByCustomerId(customerId);
        log.info("為 Customer ID: {} 查詢到 {} 筆交易紀錄", customerId, logs.size());
        return logs.stream().map(this::convertToResponse).toList();
    }

    /**
     * 透過帳號查詢該帳戶的所有交易紀錄
     */
    public List<TransLogDetailResponse> getLogsByAccount(String account) {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("帳號不可為空");
        }
        List<TransLog> logs = transLogRepository.findByOperationAccountOrderByTransactionTimeDesc(account);
        log.info("為帳號: {} 查詢到 {} 筆交易紀錄", account, logs.size());
        return logs.stream().map(this::convertToResponse).toList();
    }

    /**
     * 刪除特定交易紀錄 (專題 Demo 用)
     * 實務上銀行的 Log 絕對是 Insert-only (只能新增)，不可能允許任何人去刪除異動，
     * 但為了專案展示上的 CRUD 完整性，這裡還是保留刪除功能。
     */
    @Transactional
    public void deleteLogByReferenceId(String referenceId) {
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("Reference ID 不可為空");
        }
        // 刪除前先查詢確認存在，若不存在就由 orElseThrow 丟例外直接阻擋後續操作
        TransLog logEntity = transLogRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new RuntimeException("交易紀錄不存在，無法刪除"));
        
        transLogRepository.deleteByReferenceId(referenceId);
        log.warn("警告：已成功透過 Reference ID 刪除交易紀錄: {}", referenceId);
    }

    /**
     * 動態條件分頁查詢 (最強大的查詢方法)
     * 前端只要傳入一個 request，後面包含總筆數、總頁數、當頁資料都會一次幫你算好包進 Page 物件！
     */
    public Page<TransLogDetailResponse> searchLogs(TransLogQueryRequest request) {
        // 1. 業務邏輯防呆：檢查結束時間不可早於開始時間
        LocalDate endDate = validateEndDate(request.startDate(), request.endDate());

        // 2. 準備分頁物件
        // 【注意】Spring Data JPA 的 PageRequest 頁碼是從 0 開始算的，但前端 UI 通常是從 1 開始，所以接收進來要 - 1
        Pageable pageable = PageRequest.of(
                request.page() - 1, 
                request.size(), 
                Sort.by(Sort.Direction.DESC, "transactionTime") // 強制按交易時間倒序排列
        );

        // 3. 呼叫 Specification 產出動態的 WHERE 條件
        var spec = TransLogSpecification.dynamicQuery(
                request.customerId(),
                request.account(),
                request.startDate(),
                endDate
        );

        // 4. 一次搞定：JPA 會自動幫我們下兩道 SQL：一道 SELECT 查當頁資料，一道 SELECT COUNT 查總計筆數
        Page<TransLog> pageResult = transLogRepository.findAll(spec, pageable);
        log.info("交易紀錄動態查詢完成，共取得 {} 筆資料", pageResult.getTotalElements());

        // 5. 實體轉換 DTO
        // Page.map() 是一個超好用的語法糖，會自動將原本 Page 內的 Entity 迴圈轉為 DTO 格式，保留所有分頁資訊
        return pageResult.map(this::convertToResponse);
    }

    /**
     * 驗證結束日期是否在開始日期之前
     * 若日期合法回傳原始 endDate，若不合法紀錄警告並回傳 null (忽略結束時間條件)。
     */
    private LocalDate validateEndDate(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            log.warn("查詢日期區間不合法，結束日期 {} 在開始日期 {} 之前", endDate, startDate);
            return null;
        }
        return endDate;
    }

    /**
     * 實體轉 DTO 封裝方法
     */
    private TransLogDetailResponse convertToResponse(TransLog transLog) {
        return new TransLogDetailResponse(
                transLog.getReferenceId(),
                transLog.getAmount(),
                transLog.getType(),
                transLog.getOperationAccount(),
                transLog.getOtherAccount(),
                transLog.getBalance(),
                transLog.getTransactionTime(),
                transLog.getNote()
        );
    }
}
