<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ispan.bankmanagement.loan.vo.LoanApplyBean" %>
<%
    String selectedStatus = request.getParameter("status");
    String paramMin = request.getParameter("minAmount");
    String paramMax = request.getParameter("maxAmount");
%>
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

        th.sortable {
            cursor: pointer;
            user-select: none;
            position: relative;
        }
        th.sortable:hover {
            background: #485460;
        }
        th .arrow {
            font-size: 10px;
            margin-left: 4px;
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

<div style="margin:10px 0;">
    狀態：
    <select id="statusFilter" onchange="doFilter()">
        <option value="">全部</option>
        <option value="PENDING" <%= "PENDING".equals(selectedStatus) ? "selected" : "" %>>待審核</option>
        <option value="PENDING_CONFIRM" <%= "PENDING_CONFIRM".equals(selectedStatus) ? "selected" : "" %>>待客戶確認</option>
        <option value="APPROVED" <%= "APPROVED".equals(selectedStatus) ? "selected" : "" %>>已核准</option>
        <option value="REJECTED" <%= "REJECTED".equals(selectedStatus) ? "selected" : "" %>>已拒絕</option>
    </select>

    &nbsp;&nbsp;申請金額：
    <input type="number" id="minAmount" placeholder="最小金額" style="width:100px;"
           value="<%= paramMin != null ? paramMin : "" %>">
    ～
    <input type="number" id="maxAmount" placeholder="最大金額" style="width:100px;"
           value="<%= paramMax != null ? paramMax : "" %>">
    <button onclick="doFilter()" style="padding:4px 12px;">查詢</button>
</div>
<table>
    <tr>
        <th class="sortable" onclick="sortTable(0)">申請編號 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(1)">客戶ID <span class="arrow"></span></th>

        <th class="sortable" onclick="sortTable(2)">貸款種類 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(3)">申請金額 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(4)">期數 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(5)">申請利率 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(6)">建立時間 <span class="arrow"></span></th>

        <th class="sortable" onclick="sortTable(7)">核准金額 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(8)">核准期數 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(9)">核准利率 <span class="arrow"></span></th>

        <th class="sortable" onclick="sortTable(10)">核准人員 <span class="arrow"></span></th>
        <th class="sortable" onclick="sortTable(11)">核准時間 <span class="arrow"></span></th>

        <th class="sortable" onclick="sortTable(12)">狀態 <span class="arrow"></span></th>
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
    // ⭐ 篩選查詢（狀態 + 金額區間）
    // ===============================
    function doFilter() {
        var status = document.getElementById("statusFilter").value;
        var minAmt = document.getElementById("minAmount").value;
        var maxAmt = document.getElementById("maxAmount").value;

        var params = [];
        if (status) params.push("status=" + status);
        if (minAmt) params.push("minAmount=" + minAmt);
        if (maxAmt) params.push("maxAmount=" + maxAmt);

        location.href = "LoanApply" + (params.length > 0 ? "?" + params.join("&") : "");
    }

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

    // ===============================
    // ⭐ 表格欄位排序
    // ===============================
    var currentSortCol = -1;
    var currentSortAsc = true;

    function sortTable(colIndex) {
        var table = document.querySelector("table");
        var rows = Array.from(table.querySelectorAll("tr"));
        var headerRow = rows.shift(); // 移除表頭

        // 如果沒有資料列就不排序
        if (rows.length === 0) return;
        // 如果只有一列且是 "目前沒有資料"，不排序
        if (rows.length === 1 && rows[0].querySelector("td[colspan]")) return;

        // 決定排序方向
        if (currentSortCol === colIndex) {
            currentSortAsc = !currentSortAsc;
        } else {
            currentSortCol = colIndex;
            currentSortAsc = true;
        }

        // 排序
        rows.sort(function(a, b) {
            var cellA = a.cells[colIndex];
            var cellB = b.cells[colIndex];
            if (!cellA || !cellB) return 0;

            var valA = cellA.textContent.trim();
            var valB = cellB.textContent.trim();

            // 嘗試數字比較（移除 % 符號）
            var numA = parseFloat(valA.replace(/[%,]/g, ""));
            var numB = parseFloat(valB.replace(/[%,]/g, ""));

            if (!isNaN(numA) && !isNaN(numB)) {
                return currentSortAsc ? numA - numB : numB - numA;
            }

            // 空值排到最後
            if (valA === "" && valB !== "") return 1;
            if (valA !== "" && valB === "") return -1;
            if (valA === "" && valB === "") return 0;

            // 文字比較
            var cmp = valA.localeCompare(valB, "zh-TW");
            return currentSortAsc ? cmp : -cmp;
        });

        // 重新插入排序後的列
        rows.forEach(function(row) {
            table.appendChild(row);
        });

        // 更新箭頭
        document.querySelectorAll("th .arrow").forEach(function(el) {
            el.textContent = "";
        });
        var arrows = document.querySelectorAll("th.sortable .arrow");
        if (arrows[colIndex]) {
            arrows[colIndex].textContent = currentSortAsc ? " ▲" : " ▼";
        }
    }
</script>

</body>
</html>