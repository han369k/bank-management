package com.ispan.bankmanagement.customer;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    // 列表
    @GetMapping
    public ResponseEntity<List<CustomerVo>> list() {

        return ResponseEntity.ok(service.findAll());
    }

    // 依身分證查詢
    @GetMapping("/{idNumber}")
    public ResponseEntity<?> search(@PathVariable String idNumber) {
        CustomerVo customer = service.findByIdNumber(idNumber);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "查無此身分證字號的顧客：" + idNumber));
        }
    }

    // 新增
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody CustomerCreateReq req, 
                                  BindingResult bindingResult) {

        // 檢查表單驗證錯誤
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", "表單驗證失敗：" + errorMsg));
        }

        try {
            boolean success = service.insertCustomer(req);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "新增成功"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "系統異常，新增失敗"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "資料格式錯誤或系統異常"));
        }
    }

    // 更新狀態
    @PatchMapping("/{customerId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer customerId, 
                                          @RequestBody Map<String, String> body) {
                               
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "請提供要更新的狀態！"));
        }

        try {
            boolean success = service.updateStatus(customerId, newStatus);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "狀態更新成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "修改失敗，查無此人"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 刪除
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> delete(@PathVariable Integer customerId) {
        
        boolean success = service.deleteCustomer(customerId);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "刪除成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "刪除失敗，查無此人"));
        }
    }
}