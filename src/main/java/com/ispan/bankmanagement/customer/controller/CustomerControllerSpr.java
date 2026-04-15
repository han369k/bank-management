package com.ispan.bankmanagement.customer.controller;

import com.ispan.bankmanagement.customer.dto.CustomerCreateReq;
import com.ispan.bankmanagement.customer.service.CustomerServiceSpr;
import com.ispan.bankmanagement.customer.vo.CustomerVoSpr;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor // ✨ Lombok 自動注入 Service
public class CustomerControllerSpr {

    private final CustomerServiceSpr service;

    // ==================== GET 請求（畫面與查詢）====================

    /** R - 列出全部顧客 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("customerList", service.findAll());
        
        // ✨ 對應 src/main/resources/templates/customer-list.html
        return "customer-list"; 
    }

    /** 🌟 新增的路由：前往「新增顧客」頁面 */
    @GetMapping("/create")
    public String showCreateForm() {
        
        // ✨ 對應 src/main/resources/templates/customer-create.html
        return "customer-create"; 
    }

    /** R - 用身分證查單一顧客 */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String idNumber, Model model) {
        if (idNumber == null || idNumber.trim().isBlank()) {
            model.addAttribute("errorMessage", "請輸入身分證字號！");
            return "customer-list"; 
        }

        CustomerVoSpr customer = service.findByIdNumber(idNumber.trim());
        
        if (customer != null) {
            model.addAttribute("customerList", List.of(customer)); 
            model.addAttribute("actionMessage", "查詢結果：找到顧客「" + customer.getName() + "」");
        } else {
            model.addAttribute("actionMessage", "查無此身分證字號的顧客：" + idNumber);
        }

        return "customer-list"; 
    }

    // ==================== POST 請求（新增/修改/刪除）====================

    /** C - 新增顧客存檔 */
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute CustomerCreateReq req, 
                       BindingResult bindingResult,                  
                       RedirectAttributes ra) {

        // 1. 如果表單驗證失敗 (例如沒填姓名)
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            ra.addFlashAttribute("errorMessage", "表單驗證失敗：" + errorMsg);
            return "redirect:/customer";
        }

        // 2. 驗證通過，交給 Service 處理
        try {
            boolean success = service.insertCustomer(req);
            if (success) {
                ra.addFlashAttribute("actionMessage", "✅ 新增成功！顧客「" + req.getName() + "」已建檔。");
            } else {
                ra.addFlashAttribute("errorMessage", "❌ 新增失敗，系統異常！");
            }
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "資料格式有誤或發生系統錯誤，請重新確認！");
        }
        
        return "redirect:/customer"; 
    }

    /** U - 修改顧客狀態 */
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam String customerId, 
                               @RequestParam String newStatus, 
                               RedirectAttributes ra) {
                               
        if (customerId == null || customerId.trim().isBlank()) {
            ra.addFlashAttribute("errorMessage", "請輸入顧客代號！");
            return "redirect:/customer";
        }

        try {
            boolean success = service.updateStatus(customerId.trim(), newStatus);
            if (success) {
                ra.addFlashAttribute("actionMessage", "✅ 狀態更新成功！");
            } else {
                ra.addFlashAttribute("errorMessage", "❌ 修改失敗，找不到此顧客代號！");
            }
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }

        return "redirect:/customer";
    }

    /** D - 刪除顧客 */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String customerId, RedirectAttributes ra) {
        
        boolean success = service.deleteCustomer(customerId.trim());

        if (success) {
            ra.addFlashAttribute("actionMessage", "✅ 刪除成功！");
        } else {
            ra.addFlashAttribute("errorMessage", "❌ 刪除失敗，找不到此顧客代號！");
        }

        return "redirect:/customer";
    }
}