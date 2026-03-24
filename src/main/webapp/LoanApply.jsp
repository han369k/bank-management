<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ispan.bankmanagement.loan.vo.LoanApplyBean" %>
<%String selectedStatus = request.getParameter("status");%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>貸款申請列表</title>

    <style>
        body {
            font-family: Arial;
            background: #f5f6fa;
            padding: 20px;
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

        .PENDING { color: orange; }
        .PENDING_CONFIRM { color: #0097e6; }
        .APPROVED { color: green; }
        .REJECTED { color: red; }

        .header-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .btn-delete {
            padding: 8px 16px;
            background: #e84118;
            color: white;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-delete:hover {
            background: #c23616;
        }
    </style>

</head>

<body>

<div class="header-row">
    <h2>📊 貸款申請總表（內部系統）</h2>
    <% if ("REJECTED".equals(selectedStatus)) { %>
    <form action="${pageContext.request.contextPath}/LoanApply" method="post" style="display:inline;"
          onsubmit="return confirm('確定要刪除所有已拒絕的資料嗎？此操作無法復原！');">
        <input type="hidden" name="action" value="deleteRejected">
        <input type="hidden" name="filterStatus" value="REJECTED">
        <button class="btn-delete">🗑 刪除資料（僅限功能展示使用）</button>
    </form>
    <% } %>
</div>
<select id="statusFilter" onchange="location.href='LoanApply?status=' + this.value">
    <option value="">全部</option>
    <option value="PENDING" <%= "PENDING".equals(selectedStatus) ? "selected" : "" %>>待審核</option>
    <option value="PENDING_CONFIRM" <%= "PENDING_CONFIRM".equals(selectedStatus) ? "selected" : "" %>>待客戶確認</option>
    <option value="APPROVED" <%= "APPROVED".equals(selectedStatus) ? "selected" : "" %>>已核准</option>
    <option value="REJECTED" <%= "REJECTED".equals(selectedStatus) ? "selected" : "" %>>已拒絕</option>
</select>
<br>
<table>
    <tr>
        <th>申請編號</th>
        <th>客戶ID</th>

        <th>貸款種類</th>
        <th>申請金額</th>
        <th>期數</th>
        <th>申請利率</th>
        <th>建立時間</th>

        <th>核准金額</th>
        <th>核准期數</th>
        <th>核准利率</th>

        <th>核准人員</th>
        <th>核准時間</th>

        <th>狀態</th>
        <th>操作</th>
    </tr>

    <%
        List<LoanApplyBean> list = (List<LoanApplyBean>) request.getAttribute("list");

        if (list != null && !list.isEmpty()) {
            for (LoanApplyBean loan : list) {
                String statusText = "";
                switch (loan.getStatus()) {
                    case "PENDING":
                        statusText = "待審核";
                        break;
                    case "PENDING_CONFIRM":
                        statusText = "待客戶確認";
                        break;
                    case "APPROVED":
                        statusText = "已核准";
                        break;
                    case "REJECTED":
                        statusText = "已拒絕";
                        break;
                }
    %>

    <tr>
        <td><%= loan.getApplicationId() %></td>
        <td><%= loan.getCustomerId() %></td>

        <td><%
            String typeText = "";
            switch (loan.getApplyType()) {
                case "PERSONAL": typeText = "信用貸款"; break;
                case "CAR": typeText = "汽車車貸"; break;
                case "MOTOR": typeText = "機車車貸"; break;
                case "STUDENT": typeText = "就學貸款"; break;
                case "BUSINESS": typeText = "創業貸款"; break;
                case "HOUSE": typeText = "房屋貸款"; break;
                case "LAND": typeText = "土地貸款"; break;
                default: typeText = loan.getApplyType();
            }
        %><%= typeText %></td>
        <td><%= loan.getApplyAmount() %></td>
        <td><%= loan.getApplyPeriod() %></td>

        <td>
            <%= loan.getRate() == null ? "" :
                    loan.getRate().multiply(new java.math.BigDecimal("100"))
                            .setScale(2, java.math.RoundingMode.HALF_UP) + "%" %>
        </td>

        <td><%= loan.getCreateTime() %></td>

        <td><%= loan.getApprovedAmount() == null ? "" : loan.getApprovedAmount() %></td>
        <td><%= loan.getApprovedPeriod() == null ? "" : loan.getApprovedPeriod() %></td>
        <td>
            <%= loan.getApprovedRate() == null ? "" :
                    loan.getApprovedRate().multiply(new java.math.BigDecimal("100"))
                            .setScale(2, java.math.RoundingMode.HALF_UP) + "%" %>
        </td>
        <td><%= loan.getReviewerId() == null ? "" : loan.getReviewerId() %></td>
        <td><%= loan.getReviewTime() == null ? "" : loan.getReviewTime() %></td>

        <td class="status <%= loan.getStatus() %>">
            <%= statusText %>
        </td>

        <td>

            <% if ("PENDING".equals(loan.getStatus())) { %>

            <!-- 🔵 修改方案 / 送客戶確認 -->
            <form action="${pageContext.request.contextPath}/LoanApply" method="post">
                <input type="hidden" name="filterStatus" value="<%= selectedStatus != null ? selectedStatus : "" %>">
                <input type="hidden" name="action" value="approve">
                <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
                <input type="hidden" name="applyType" value="<%= loan.getApplyType() %>">

                金額 <input type="number" name="approvedAmount" required style="width:80px;">
                期數 <select name="approvedPeriod" class="period-select"
                             data-type="<%= loan.getApplyType() %>"
                             style="width:80px;"></select>

                審核人 <input type="number" name="reviewerId" value="1" style="width:60px;">

                <button class="btn edit">送出方案</button>
            </form>

            <!-- 🟢 直接核准 -->
            <form action="${pageContext.request.contextPath}/LoanApply" method="post" style="display:inline;">
                <input type="hidden" name="filterStatus" value="<%= selectedStatus != null ? selectedStatus : "" %>">
                <input type="hidden" name="action" value="approveDirect">
                <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
                <input type="hidden" name="applyType" value="<%= loan.getApplyType() %>">
                <input type="hidden" name="approvedAmount" value="<%= loan.getApplyAmount() %>">
                <input type="hidden" name="approvedPeriod" value="<%= loan.getApplyPeriod() %>">
                審核人 <input type="number" name="reviewerId" value="1" style="width:60px;">

                <button class="btn approve">直接核准</button>
            </form>

            <!-- 🔴 拒絕 -->
            <form action="${pageContext.request.contextPath}/LoanApply" method="post" style="display:inline;">
                <input type="hidden" name="filterStatus" value="<%= selectedStatus != null ? selectedStatus : "" %>">
                <input type="hidden" name="action" value="rejectByBank">
                <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
                審核人 <input type="number" name="reviewerId" value="1" style="width:60px;">

                <button class="btn reject">拒絕</button>
            </form>

            <% } else { %>
            -
            <% } %>

        </td>
    </tr>

    <%
        }
    } else {
    %>

    <tr>
        <td colspan="14">目前沒有資料</td>
    </tr>

    <%
        }
    %>

</table>
<script>
    // ===============================
    // ⭐ 貸款種類 → 對應期數
    // ===============================
    const termOptions = {
        PERSONAL: [12,24,36,48,60],
        CAR: [12,24,36,48,60],
        MOTOR: [12,24,36],
        STUDENT: [60,84,120],
        BUSINESS: [36,60,84],
        HOUSE: [120,240,360,480],
        LAND: [120,180,240]
    };

    // ===============================
    // ⭐ 初始化所有 dropdown
    // ===============================
    document.querySelectorAll(".period-select").forEach(select => {

        const type = select.getAttribute("data-type");
        const terms = termOptions[type];

        if (!terms) return;

        // 清空
        select.innerHTML = "";

        // 建立選項
        terms.forEach(term => {
            const option = document.createElement("option");
            option.value = term;
            option.textContent = term + "期";
            select.appendChild(option);
        });
    });
</script>

</body>
</html>