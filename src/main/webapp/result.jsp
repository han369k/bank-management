<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ispan.bankmanagement.customer.vo.Customer" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <title>爪哇銀行 — 操作結果</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- ===== 頁首 ===== -->
<header class="cust-header">
    <div class="cust-header-inner">
        <h1>🏦 爪哇銀行後台管理系統</h1>
        <span class="cust-subtitle">CRUD 執行結果頁</span>
    </div>
</header>

<main class="cust-main">

    <!-- 回首頁按鈕 -->
    <div class="cust-back-bar">
        <a href="customer360.html" class="cust-btn cust-btn-secondary">← 返回顧客管理頁</a>
    </div>

    <!-- ===== 操作訊息 ===== -->
    <%
        String actionMessage = (String) request.getAttribute("actionMessage");
        String errorMessage  = (String) request.getAttribute("errorMessage");
    %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="cust-alert cust-alert-danger">
            <strong>操作失敗：</strong><%= errorMessage %>
        </div>
    <% } %>

    <% if (actionMessage != null && !actionMessage.isEmpty()) { %>
        <div class="cust-alert cust-alert-success">
            <strong>操作結果：</strong><%= actionMessage %>
        </div>
    <% } %>

    <!-- ===== 顧客資料表格（只在有資料時顯示）===== -->
    <%
        List<Customer> customerList = (List<Customer>) request.getAttribute("customerList");
    %>

    <% if (customerList != null && !customerList.isEmpty()) { %>
    <section class="cust-card">
        <h2 class="cust-section-title">📋 查詢結果（共 <%= customerList.size() %> 筆）</h2>

        <div class="cust-table-wrapper">
            <table class="cust-table">
                <thead>
                    <tr>
                        <th>顧客代號</th>
                        <th>身分證字號</th>
                        <th>姓名</th>
                        <th>生日</th>
                        <th>電話</th>
                        <th>Email</th>
                        <th>月收入</th>
                        <th>信用分數</th>
                        <th>狀態</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Customer c : customerList) { %>
                    <tr>
                        <td><strong><%= c.getCustomerId() %></strong></td>
                        <td><%= c.getIdNumber() %></td>
                        <td><%= c.getName() %></td>
                        <td><%= c.getDateOfBirth() != null ? c.getDateOfBirth() : "—" %></td>
                        <td><%= c.getPhone() != null ? c.getPhone() : "—" %></td>
                        <td><%= c.getEmail() != null ? c.getEmail() : "—" %></td>
                        <td>
                            <%= c.getIncome() != null
                                ? "NT$ " + String.format("%,.0f", c.getIncome())
                                : "—" %>
                        </td>
                        <td><%= c.getCreditScore() %></td>
                        <td>
                            <%
                                String status = c.getStatus();
                                String badgeClass = "cust-badge-default";
                                if ("Active".equals(status))     badgeClass = "cust-badge-active";
                                else if ("VIP".equals(status))   badgeClass = "cust-badge-vip";
                                else if ("Frozen".equals(status)) badgeClass = "cust-badge-frozen";
                                else if ("Blacklist".equals(status)) badgeClass = "cust-badge-blacklist";
                            %>
                            <span class="cust-badge <%= badgeClass %>"><%= status %></span>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>
    <% } else if (errorMessage == null) { %>
        <div class="cust-alert cust-alert-info">
            查無資料或操作完成，請返回繼續操作。
        </div>
    <% } %>

</main>

<footer class="cust-footer">
    <p>爪哇銀行管理系統 © 2026 | 顧客管理模組 by 以琳</p>
</footer>

</body>
</html>
