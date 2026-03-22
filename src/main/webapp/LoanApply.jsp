<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ispan.bankmanagement.loan.vo.LoanApplyBean" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>貸款申請列表</title>
<style>
        body {
            font-family: Arial;
            background: #f5f6fa;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th, td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: center;
        }

        th {
            background: #2f3640;
            color: white;
        }

        .btn {
            padding: 5px 10px;
            border: none;
            cursor: pointer;
        }

        .approve { background: #44bd32; color: white; }
        .reject { background: #e84118; color: white; }
        .edit { background: #0097e6; color: white; }

        .status {
            font-weight: bold;
        }
    </style>
</head>

<body>

<h2>📊 貸款申請總表（內部系統）</h2>

<table>
    <tr>
        <th>申請編號</th>
        <th>客戶ID</th>
        <th>申請金額</th>
        <th>期數</th>
        <th>建立時間</th>

        <th>核准金額</th>
        <th>利率</th>
        <th>核准期數</th>

        <th>狀態</th>
        <th>操作</th>
    </tr>

<%
    List<LoanApplyBean> list = (List<LoanApplyBean>) request.getAttribute("list");
    if (list != null) {
        for (LoanApplyBean loan : list) {
%>

<tr>
    <td><%= loan.getApplicationId() %></td>
    <td><%= loan.getCustomerId() %></td>
    <td><%= loan.getApplyAmount() %></td>
    <td><%= loan.getApplyPeriod() %></td>
    <td><%= loan.getCreateTime() %></td>

    <td><%= loan.getApprovedAmount() %></td>
    <td><%= loan.getApprovedRate() %></td>
    <td><%= loan.getApprovedPeriod() %></td>

    <td class="status"><%= loan.getStatus() %></td>

    <td>

        <% if ("PENDING".equals(loan.getStatus())) { %>

        <!-- 🔵 修改方案 / 送客戶確認 -->
        <form action="loanApply" method="post" style="display:inline;">
            <input type="hidden" name="action" value="approve">
            <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">

            金額<input type="number" step="0.01" name="approvedAmount" required>
            利率<input type="number" step="0.01" name="approvedRate" required>
            期數<input type="number" name="approvedPeriod" required>

            <input type="hidden" name="reviewerId" value="1">

            <button class="btn edit">送審/修改</button>
        </form>

        <!-- 🟢 直接核准 -->
        <form action="loanApply" method="post" style="display:inline;">
            <input type="hidden" name="action" value="approve">
            <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
            <input type="hidden" name="approvedAmount" value="<%= loan.getApplyAmount() %>">
            <input type="hidden" name="approvedRate" value="1.8">
            <input type="hidden" name="approvedPeriod" value="<%= loan.getApplyPeriod() %>">
            <input type="hidden" name="reviewerId" value="1">

            <button class="btn approve">直接核准</button>
        </form>

        <!-- 🔴 拒絕 -->
        <form action="loanApply" method="post" style="display:inline;">
            <input type="hidden" name="action" value="rejectByBank">
            <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
            <input type="hidden" name="reviewerId" value="1">

            <button class="btn reject">拒絕</button>
        </form>

        <% } else { %>
            -
        <% } %>

    </td>
</tr>

<%
        }
    }
%>
</body>
</html>