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
@Slf4j
public class TransLogService {

    private final TransLogRepository transLogRepository;

    public TransLogDetailResponse getLogByReferenceId(String referenceId) {
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("Reference ID 不可為空");
        }
        TransLog logEntity = transLogRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new RuntimeException("找不到該筆交易紀錄"));
        
        return convertToResponse(logEntity);
    }

    public List<TransLogDetailResponse> getLogsByCustomerId(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID 不可為空");
        }
        List<TransLog> logs = transLogRepository.findByCustomerId(customerId);
        log.info("為 Customer ID: {} 查詢到 {} 筆交易紀錄", customerId, logs.size());
        return logs.stream().map(this::convertToResponse).toList();
    }

    public List<TransLogDetailResponse> getLogsByAccount(String account) {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("帳號不可為空");
        }
        List<TransLog> logs = transLogRepository.findByOperationAccountOrderByTransactionTimeDesc(account);
        log.info("為帳號: {} 查詢到 {} 筆交易紀錄", account, logs.size());
        return logs.stream().map(this::convertToResponse).toList();
    }

    @Transactional
    public void deleteLogByReferenceId(String referenceId) {
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("Reference ID 不可為空");
        }
        TransLog logEntity = transLogRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new RuntimeException("交易紀錄不存在，無法刪除"));
        
        transLogRepository.deleteByReferenceId(referenceId);
        log.warn("警告：已成功透過 Reference ID 刪除交易紀錄: {}", referenceId);
    }

    public Page<TransLogDetailResponse> searchLogs(TransLogQueryRequest request) {
        LocalDate endDate = validateEndDate(request.startDate(), request.endDate());

        // Spring Data JPA 的 PageRequest 頁碼是從 0 開始的，所以需要 - 1
        Pageable pageable = PageRequest.of(
                request.page() - 1, 
                request.size(), 
                Sort.by(Sort.Direction.DESC, "transactionTime")
        );

        var spec = TransLogSpecification.dynamicQuery(
                request.customerId(),
                request.account(),
                request.startDate(),
                endDate
        );

        Page<TransLog> pageResult = transLogRepository.findAll(spec, pageable);
        log.info("交易紀錄動態查詢完成，共取得 {} 筆資料", pageResult.getTotalElements());

        // Page.map() 會自動將原本實體的 Page 轉換為我們需要的 DTO Page，極其方便
        return pageResult.map(this::convertToResponse);
    }

    private LocalDate validateEndDate(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            log.warn("查詢日期區間不合法，結束日期 {} 在開始日期 {} 之前", endDate, startDate);
            return null;
        }
        return endDate;
    }

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
