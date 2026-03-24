<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ispan.bankmanagement.loan.vo.LoanApplyBean" %>
<%
    String selectedStatus = request.getParameter("status");
    String paramMin = request.getParameter("minAmount");
    String paramMax = request.getParameter("maxAmount");
%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>貸款申請審核總表</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css"> <style>
    th.sortable { cursor: pointer; user-select: none; position: relative; }
    th.sortable:hover { background-color: #f1f3f5; }
    .badge-pending { background-color: #fd7e14; color: white; }
    .badge-confirm { background-color: #0dcaf0; color: white; }
    .badge-approved { background-color: #198754; color: white; }
    .badge-rejected { background-color: #dc3545; color: white; }
</style>
</head>

<body>

<nav class="navbar navbar-expand-lg">
    <div class="container-fluid px-4">
        <a class="navbar-brand" href="index.html"><i class="bi bi-bank me-2"></i>Bank Management (後台)</a>
        <div class="navbar-nav ms-3">
            <a class="nav-link active" href="LoanApply">貸款審核總表</a>
        </div>
    </div>
</nav>

<div class="page-wrapper" style="max-width: 95%; margin: 0 auto;">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0"><i class="bi bi-clipboard-data me-2"></i>貸款申請審核總表</h3>
        <% if ("REJECTED".equals(selectedStatus)) { %>
        <form action="${pageContext.request.contextPath}/LoanApply" method="post" style="display:inline;" onsubmit="return confirm('確定要刪除所有已拒絕的資料嗎？此操作無法復原！');">
            <input type="hidden" name="action" value="deleteRejected">
            <input type="hidden" name="filterStatus" value="REJECTED">
            <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> 刪除資料 (Demo用)</button>
        </form>
        <% } %>
    </div>

    <div class="card mb-4">
        <div class="card-body py-2 px-3 d-flex align-items-center gap-3">
            <div class="d-flex align-items-center gap-2">
                <span class="text-muted small fw-bold">狀態：</span>
                <select id="statusFilter" class="form-select form-select-sm" style="width:auto;" onchange="doFilter()">
                    <option value="">全部</option>
                    <option value="PENDING" <%= "PENDING".equals(selectedStatus) ? "selected" : "" %>>待審核</option>
                    <option value="PENDING_CONFIRM" <%= "PENDING_CONFIRM".equals(selectedStatus) ? "selected" : "" %>>待客戶確認</option>
                    <option value="APPROVED" <%= "APPROVED".equals(selectedStatus) ? "selected" : "" %>>已核准</option>
                    <option value="REJECTED" <%= "REJECTED".equals(selectedStatus) ? "selected" : "" %>>已拒絕</option>
                </select>
            </div>
            <div class="d-flex align-items-center gap-2">
                <span class="text-muted small fw-bold">申請金額：</span>
                <input type="number" id="minAmount" class="form-control form-control-sm" placeholder="最小金額" style="width:120px;" value="<%= paramMin != null ? paramMin : "" %>">
                <span>～</span>
                <input type="number" id="maxAmount" class="form-control form-control-sm" placeholder="最大金額" style="width:120px;" value="<%= paramMax != null ? paramMax : "" %>">
            </div>
            <button class="btn btn-sm btn-dark" onclick="doFilter()"><i class="bi bi-search"></i> 查詢</button>
        </div>
    </div>

    <div class="card">
        <div class="table-responsive">
            <table class="table table-hover table-bordered align-middle mb-0" style="font-size: 13px;">
                <thead class="table-light">
                <tr>
                    <th class="sortable" onclick="sortTable(0)">申請編號 <span class="arrow"></span></th>
                    <th class="sortable" onclick="sortTable(1)">客戶ID <span class="arrow"></span></th>
                    <th class="sortable" onclick="sortTable(2)">貸款種類 <span class="arrow"></span></th>
                    <th class="sortable text-end" onclick="sortTable(3)">申請金額 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(4)">期數 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(5)">申請利率 <span class="arrow"></span></th>
                    <th class="sortable text-end" onclick="sortTable(6)">核准金額 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(7)">核准期數 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(8)">核准利率 <span class="arrow"></span></th>
                    <th class="sortable" onclick="sortTable(9)">狀態 <span class="arrow"></span></th>
                    <th class="text-center">操作 (僅限待審核)</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<LoanApplyBean> list = (List<LoanApplyBean>) request.getAttribute("list");
                    if (list != null && !list.isEmpty()) {
                        for (LoanApplyBean loan : list) {
                            String statusText = "";
                            String badgeClass = "";
                            switch (loan.getStatus()) {
                                case "PENDING": statusText = "待審核"; badgeClass = "badge-pending"; break;
                                case "PENDING_CONFIRM": statusText = "待客戶確認"; badgeClass = "badge-confirm"; break;
                                case "APPROVED": statusText = "已核准"; badgeClass = "badge-approved"; break;
                                case "REJECTED": statusText = "已拒絕"; badgeClass = "badge-rejected"; break;
                            }
                %>
                <tr>
                    <td><span class="font-monospace"><%= loan.getApplicationId() %></span></td>
                    <td><%= loan.getCustomerId() %></td>
                    <td>
                        <% String typeText = "";
                            switch (loan.getApplyType()) {
                                case "PERSONAL": typeText = "信用貸款"; break;
                                case "CAR": typeText = "汽車車貸"; break;
                                case "MOTOR": typeText = "機車車貸"; break;
                                case "STUDENT": typeText = "就學貸款"; break;
                                case "BUSINESS": typeText = "創業貸款"; break;
                                case "HOUSE": typeText = "房屋貸款"; break;
                                case "LAND": typeText = "土地貸款"; break;
                                default: typeText = loan.getApplyType();
                            } %><%= typeText %>
                    </td>
                    <td class="text-end fw-bold"><%= loan.getApplyAmount() %></td>
                    <td class="text-center"><%= loan.getApplyPeriod() %></td>
                    <td class="text-center"><%= loan.getRate() == null ? "" : loan.getRate().multiply(new java.math.BigDecimal("100")).setScale(2, java.math.RoundingMode.HALF_UP) + "%" %></td>

                    <td class="text-end text-success"><%= loan.getApprovedAmount() == null ? "-" : loan.getApprovedAmount() %></td>
                    <td class="text-center text-success"><%= loan.getApprovedPeriod() == null ? "-" : loan.getApprovedPeriod() %></td>
                    <td class="text-center text-success"><%= loan.getApprovedRate() == null ? "-" : loan.getApprovedRate().multiply(new java.math.BigDecimal("100")).setScale(2, java.math.RoundingMode.HALF_UP) + "%" %></td>

                    <td><span class="badge <%= badgeClass %>"><%= statusText %></span></td>

                    <td class="text-center">
                        <% if ("PENDING".equals(loan.getStatus())) { %>
                        <div class="d-flex justify-content-center align-items-center gap-1">
                            <input type="number" id="reviewer_<%= loan.getApplicationId() %>" class="form-control form-control-sm" value="1" style="width:45px; padding:2px;" title="審核人ID">
                            <button class="btn btn-sm btn-outline-primary" style="font-size:11px" onclick="openModal('<%= loan.getApplicationId() %>', '<%= loan.getApplyType() %>')">修改</button>

                            <form action="${pageContext.request.contextPath}/LoanApply" method="post" class="m-0">
                                <input type="hidden" name="filterStatus" value="<%= selectedStatus != null ? selectedStatus : "" %>">
                                <input type="hidden" name="action" value="approveDirect">
                                <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
                                <input type="hidden" name="applyType" value="<%= loan.getApplyType() %>">
                                <input type="hidden" name="approvedAmount" value="<%= loan.getApplyAmount() %>">
                                <input type="hidden" name="approvedPeriod" value="<%= loan.getApplyPeriod() %>">
                                <input type="hidden" name="reviewerId">
                                <button type="submit" class="btn btn-sm btn-success" style="font-size:11px" onclick="this.form.reviewerId.value=document.getElementById('reviewer_<%= loan.getApplicationId() %>').value"><i class="bi bi-check-lg"></i></button>
                            </form>

                            <form action="${pageContext.request.contextPath}/LoanApply" method="post" class="m-0">
                                <input type="hidden" name="filterStatus" value="<%= selectedStatus != null ? selectedStatus : "" %>">
                                <input type="hidden" name="action" value="rejectByBank">
                                <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
                                <input type="hidden" name="reviewerId">
                                <button type="submit" class="btn btn-sm btn-danger" style="font-size:11px" onclick="this.form.reviewerId.value=document.getElementById('reviewer_<%= loan.getApplicationId() %>').value"><i class="bi bi-x-lg"></i></button>
                            </form>
                        </div>
                        <% } else { %> <span class="text-muted">-</span> <% } %>
                    </td>
                </tr>
                <%  }
                } else { %>
                <tr><td colspan="11" class="text-center py-4 text-muted">目前沒有資料</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="approveModal" tabindex="-1">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fs-6"><i class="bi bi-pencil-square me-1"></i>修改貸款方案</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="modal_applicationId">
                <input type="hidden" id="modal_applyType">
                <div class="mb-3">
                    <label class="form-label text-muted small">新方案金額</label>
                    <input type="number" id="modal_amount" class="form-control" min="1" required>
                </div>
                <div class="mb-3">
                    <label class="form-label text-muted small">新方案期數</label>
                    <select id="modal_period" class="form-select"></select>
                </div>
            </div>
            <div class="modal-footer p-2">
                <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">取消</button>
                <button type="button" class="btn btn-sm btn-primary" onclick="submitApprove()">確認送出</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const termOptions = {
        PERSONAL: [12,24,36,48,60], CAR: [12,24,36,48,60], MOTOR: [12,24,36],
        STUDENT: [60,84,120], BUSINESS: [36,60,84], HOUSE: [120,240,360,480], LAND: [120,180,240]
    };

    var currentFilterStatus = "<%= selectedStatus != null ? selectedStatus : "" %>";
    var myModal = null; // 用來存放 Bootstrap Modal 實例

    document.addEventListener("DOMContentLoaded", function() {
        myModal = new bootstrap.Modal(document.getElementById('approveModal'));
    });

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

    function openModal(applicationId, applyType) {
        document.getElementById("modal_applicationId").value = applicationId;
        document.getElementById("modal_applyType").value = applyType;
        document.getElementById("modal_amount").value = "";

        var periodSelect = document.getElementById("modal_period");
        periodSelect.innerHTML = "";
        var terms = termOptions[applyType];
        if (terms) {
            terms.forEach(function(t) {
                var opt = document.createElement("option");
                opt.value = t; opt.textContent = t + "期";
                periodSelect.appendChild(opt);
            });
        }
        myModal.show();
    }

    function submitApprove() {
        var applicationId = document.getElementById("modal_applicationId").value;
        var applyType = document.getElementById("modal_applyType").value;
        var amount = document.getElementById("modal_amount").value;
        var period = document.getElementById("modal_period").value;
        var reviewerId = document.getElementById("reviewer_" + applicationId).value;

        if (!amount || parseInt(amount) <= 0) { alert("請輸入有效的方案金額"); return; }

        var form = document.createElement("form");
        form.method = "POST";
        form.action = "LoanApply";
        var fields = { action: "approve", filterStatus: currentFilterStatus, applicationId: applicationId, applyType: applyType, approvedAmount: amount, approvedPeriod: period, reviewerId: reviewerId };

        for (var key in fields) {
            var input = document.createElement("input");
            input.type = "hidden"; input.name = key; input.value = fields[key];
            form.appendChild(input);
        }
        document.body.appendChild(form);
        form.submit();
    }

    // 簡易排序邏輯
    var currentSortCol = -1; var currentSortAsc = true;
    function sortTable(colIndex) {
        var table = document.querySelector("table tbody");
        var rows = Array.from(table.querySelectorAll("tr"));
        if (rows.length <= 1) return;

        if (currentSortCol === colIndex) { currentSortAsc = !currentSortAsc; }
        else { currentSortCol = colIndex; currentSortAsc = true; }

        rows.sort(function(a, b) {
            var valA = a.cells[colIndex].textContent.trim().replace(/[%,]/g, "");
            var valB = b.cells[colIndex].textContent.trim().replace(/[%,]/g, "");
            var numA = parseFloat(valA); var numB = parseFloat(valB);

            if (!isNaN(numA) && !isNaN(numB)) return currentSortAsc ? numA - numB : numB - numA;
            return currentSortAsc ? valA.localeCompare(valB, "zh-TW") : -valA.localeCompare(valB, "zh-TW");
        });
        rows.forEach(function(row) { table.appendChild(row); });

        document.querySelectorAll("th .arrow").forEach(function(el) { el.textContent = ""; });
        var arrows = document.querySelectorAll("th.sortable .arrow");
        if (arrows[colIndex]) { arrows[colIndex].textContent = currentSortAsc ? " ▲" : " ▼"; }
    }
</script>

</body>
</html>