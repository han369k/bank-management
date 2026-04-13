package com.ispan.bankmanagement.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Controller — 接電話的客服人員（Servlet）
 *
 * URL 對應：/CustomerController
 *
 * 這支 Servlet 處理所有顧客 CRUD 操作。
 * 用一個叫做 "action" 的參數來判斷要做什麼事情：
 *
 * action=list   → 查詢全部顧客（GET）
 * action=search → 用身分證查詢（GET）
 * action=insert → 新增顧客（POST）
 * action=update → 修改狀態（POST）
 * action=delete → 刪除顧客（POST）
 */
@WebServlet("/CustomerController")
public class CustomerController extends HttpServlet {

    private CustomerDao customerDao = new CustomerDao();

    // ==================== GET 請求（查詢用）====================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 設定中文不亂碼
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list"; // 預設動作：列出全部

        switch (action) {
            case "list":
                handleList(request, response);
                break;
            case "search":
                handleSearch(request, response);
                break;
            default:
                handleList(request, response);
        }
    }

    // ==================== POST 請求（新增/修改/刪除用）====================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 設定中文不亂碼（重要！表單輸入有中文時必須加）
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "insert":
                handleInsert(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "delete":
                handleDelete(request, response);
                break;
            default:
                // 動作不明，導回新增頁面
                response.sendRedirect(request.getContextPath() + "/view/customer-create.html");
        }
    }

    // ==================== 私有處理方法 ====================

    /** R - 列出全部顧客 */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Customer> customerList = customerDao.findAll();

        // 把資料放進 request，讓 JSP 可以取得
        request.setAttribute("customerList", customerList);
        request.setAttribute("actionMessage", "顯示全部顧客（共 " + customerList.size() + " 筆）");

        // 轉向結果頁面
        request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
    }

    /** R - 用身分證查單一顧客 */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idNumber = request.getParameter("idNumber");

        if (idNumber == null || idNumber.trim().isEmpty()) {
            request.setAttribute("errorMessage", "請輸入身分證字號！");
            request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
            return;
        }

        Customer customer = customerDao.findByIdNumber(idNumber.trim());

        if (customer != null) {
            // 把找到的顧客放進一個 List，這樣 JSP 可以用同一個表格顯示
            List<Customer> resultList = new java.util.ArrayList<>();
            resultList.add(customer);
            request.setAttribute("customerList", resultList);
            request.setAttribute("actionMessage", "查詢結果：找到顧客「" + customer.getName() + "」");
        } else {
            request.setAttribute("actionMessage", "查無此身分證字號的顧客：" + idNumber);
        }

        request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
    }

    /** C - 新增顧客 */
    private void handleInsert(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 從表單收集資料
        String customerId = request.getParameter("customerId");
        String idNumber   = request.getParameter("idNumber");
        String name       = request.getParameter("name");
        String dobStr     = request.getParameter("dateOfBirth");   // yyyy-MM-dd
        String address    = request.getParameter("address");
        String phone      = request.getParameter("phone");
        String email      = request.getParameter("email");
        String incomeStr  = request.getParameter("income");
        String creditStr  = request.getParameter("creditScore");

        // 把資料裝進 Customer 物件
        Customer c = new Customer();
        c.setCustomerId(customerId);
        c.setIdNumber(idNumber);
        c.setName(name);
        c.setAddress(address);
        c.setPhone(phone);
        c.setEmail(email);

        // 型別轉換（把字串轉成正確的資料型別）
        try {
            if (dobStr != null && !dobStr.isEmpty()) {
                c.setDateOfBirth(Date.valueOf(dobStr)); // "yyyy-MM-dd" → java.sql.Date
            }
            if (incomeStr != null && !incomeStr.isEmpty()) {
                c.setIncome(new BigDecimal(incomeStr));
            }
            if (creditStr != null && !creditStr.isEmpty()) {
                c.setCreditScore(Integer.parseInt(creditStr));
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "資料格式有誤，請重新確認！");
            request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
            return;
        }

        // 呼叫 DAO 執行 INSERT
        boolean success = customerDao.insertCustomer(c);

        if (success) {
            request.setAttribute("actionMessage", "✅ 新增成功！顧客「" + name + "」已建檔。");
        } else {
            request.setAttribute("errorMessage", "❌ 新增失敗，可能是顧客代號或身分證已存在！");
        }

        request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
    }

    /** U - 修改顧客狀態 */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customerId");
        String newStatus  = request.getParameter("newStatus");

        if (customerId == null || customerId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "請輸入顧客代號！");
            request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
            return;
        }

        boolean success = customerDao.updateStatus(customerId.trim(), newStatus);

        if (success) {
            request.setAttribute("actionMessage", "✅ 修改成功！顧客 " + customerId + " 狀態已更新為「" + newStatus + "」。");
        } else {
            request.setAttribute("errorMessage", "❌ 修改失敗，找不到此顧客代號：" + customerId);
        }

        request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
    }

    /** D - 刪除顧客 */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customerId");

        if (customerId == null || customerId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "請輸入顧客代號！");
            request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
            return;
        }

        boolean success = customerDao.deleteCustomer(customerId.trim());

        if (success) {
            request.setAttribute("actionMessage", "✅ 刪除成功！顧客 " + customerId + " 已從系統中移除。");
        } else {
            request.setAttribute("errorMessage", "❌ 刪除失敗，找不到此顧客代號：" + customerId);
        }

        request.getRequestDispatcher("/view/customer-result.jsp").forward(request, response);
    }
}