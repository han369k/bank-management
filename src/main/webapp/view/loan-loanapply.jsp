<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ispan.bankmanagement.loan.LoanApplyBean" %>
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
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .page-wrapper { max-width: 95%; margin: 32px auto; padding: 0 16px; }
        .card-box {
            background: #fff; border-radius: 8px;
            box-shadow: 0 1px 4px rgba(0,0,0,.08); border: 1px solid #eee;
        }
        th.sortable { cursor: pointer; user-select: none; position: relative; }
        th.sortable:hover { background-color: #f8f9fa !important; }

        .badge { font-weight: 600; padding: 5px 10px; border-radius: 6px; letter-spacing: 0.5px; }
        .badge-pending { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
        .badge-confirm { background-color: #cff4fc; color: #055160; border: 1px solid #b6effb; }
        .badge-approved { background-color: #d1e7dd; color: #0f5132; border: 1px solid #badbcc; }
        .badge-rejected { background-color: #f8d7da; color: #842029; border: 1px solid #f5c2c7; }

        .reviewer-input { width: 50px; text-align: center; font-size: 12px; }
    </style>
</head>

<body>

<nav class="navbar navbar-expand-lg">
    <div class="container-fluid px-4">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/index.html">
            <i class="bi bi-bank me-2"></i>Bank Management <span class="fs-6 text-white-50">(後台管理)</span>
        </a>
        <div class="navbar-nav ms-3">
            <a class="nav-link active" href="LoanApply">貸款審核總表</a>
        </div>
    </div>
</nav>

<div class="page-wrapper">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="page-title mb-0"><i class="bi bi-clipboard-data me-2"></i>貸款申請審核總表</h2>
        <% if ("REJECTED".equals(selectedStatus)) { %>
        <form id="deleteRejectedForm" action="${pageContext.request.contextPath}/LoanApply" method="post" style="display:inline;">
            <input type="hidden" name="action" value="deleteRejected">
            <input type="hidden" name="filterStatus" value="REJECTED">
            <button type="button" class="btn btn-sm btn-outline-danger" onclick="confirmDeleteRejected()">
                <i class="bi bi-trash3 me-1"></i>清除已拒絕案件 <span class="small">(Demo用)</span>
            </button>
        </form>
        <% } %>
    </div>

    <div class="card-box mb-4 p-3 d-flex flex-wrap align-items-center gap-4">
        <div class="d-flex align-items-center gap-2">
            <span class="text-muted small fw-bold text-uppercase">案件狀態</span>
            <select id="statusFilter" class="form-select form-select-sm" style="width: 150px;" onchange="doFilter()">
                <option value="">全部</option>
                <option value="PENDING" <%= "PENDING".equals(selectedStatus) ? "selected" : "" %>>待審核</option>
                <option value="PENDING_CONFIRM" <%= "PENDING_CONFIRM".equals(selectedStatus) ? "selected" : "" %>>待客戶確認</option>
                <option value="APPROVED" <%= "APPROVED".equals(selectedStatus) ? "selected" : "" %>>已核准</option>
                <option value="REJECTED" <%= "REJECTED".equals(selectedStatus) ? "selected" : "" %>>已拒絕</option>
            </select>
        </div>

        <div class="d-flex align-items-center gap-2">
            <span class="text-muted small fw-bold text-uppercase">申請金額區間</span>
            <input type="number" id="minAmount" class="form-control form-control-sm" placeholder="最小金額" style="width:120px;" value="<%= paramMin != null ? paramMin : "" %>">
            <span class="text-muted">～</span>
            <input type="number" id="maxAmount" class="form-control form-control-sm" placeholder="最大金額" style="width:120px;" value="<%= paramMax != null ? paramMax : "" %>">
        </div>
        <div class="ms-auto">
            <button class="btn btn-sm btn-dark px-3" onclick="doFilter()"><i class="bi bi-search me-1"></i>查詢</button>
        </div>
    </div>

    <div class="card-box p-0 overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0" style="font-size: 12px;">
                <thead class="table-light text-muted">
                <tr>
                    <th class="sortable ps-3" onclick="sortTable(0)">申請編號 <span class="arrow"></span></th>
                    <th class="sortable" onclick="sortTable(1)">客戶ID <span class="arrow"></span></th>
                    <th class="sortable" onclick="sortTable(2)">貸款種類 <span class="arrow"></span></th>
                    <th class="sortable text-end" onclick="sortTable(3)">申請金額 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(4)">期數 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(5)">申請利率 <span class="arrow"></span></th>

                    <th class="sortable text-center" onclick="sortTable(6)">申請時間 <span class="arrow"></span></th>

                    <th class="sortable text-end text-success" onclick="sortTable(7)">核准金額 <span class="arrow"></span></th>
                    <th class="sortable text-center text-success" onclick="sortTable(8)">核准期數 <span class="arrow"></span></th>

                    <th class="sortable text-center text-success" onclick="sortTable(9)">審核時間 <span class="arrow"></span></th>

                    <th class="sortable text-center text-success" onclick="sortTable(10)">核准利率 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(11)">審核人員 <span class="arrow"></span></th>
                    <th class="sortable text-center" onclick="sortTable(12)">狀態 <span class="arrow"></span></th>
                    <th class="text-center pe-3">操作 (限待審核)</th>
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
                    <td class="ps-3"><span class="font-monospace text-muted"><%= loan.getApplicationId() %></span></td>
                    <td><span class="font-monospace text-primary"><%= loan.getCustomerId() %></span></td>
                    <td>
                        <%
                            String typeText = "";
                            String applyType = loan.getApplyType();

                            if (applyType == null || applyType.trim().isEmpty()) {
                                typeText = "<span class='text-danger'>未填寫/未知</span>";
                            } else {
                                switch (applyType) {
                                    case "PERSONAL": typeText = "信用貸款"; break;
                                    case "CAR": typeText = "汽車車貸"; break;
                                    case "MOTOR": typeText = "機車車貸"; break;
                                    case "STUDENT": typeText = "就學貸款"; break;
                                    case "BUSINESS": typeText = "創業貸款"; break;
                                    case "HOUSE": typeText = "房屋貸款"; break;
                                    case "LAND": typeText = "土地貸款"; break;
                                    default: typeText = applyType;
                                }
                            }
                        %>
                        <%= typeText %>
                    </td>
                    <td class="text-end fw-bold">$ <%= String.format("%,d", loan.getApplyAmount()) %></td>
                    <td class="text-center"><%= loan.getApplyPeriod() %> 期</td>
                    <td class="text-center"><%= loan.getRate() == null ? "" : loan.getRate().multiply(new java.math.BigDecimal("100")).setScale(2, java.math.RoundingMode.HALF_UP) + "%" %></td>

                    <td class="text-center text-muted small"><%= loan.getCreateTime() != null ? String.valueOf(loan.getCreateTime()).replace("T", " ") : "-" %></td>

                    <td class="text-end text-success fw-bold"><%= loan.getApprovedAmount() == null ? "-" : "$ " + String.format("%,d", loan.getApprovedAmount()) %></td>
                    <td class="text-center text-success"><%= loan.getApprovedPeriod() == null ? "-" : loan.getApprovedPeriod() + " 期" %></td>

                    <td class="text-center text-success small"><%= loan.getReviewTime() != null ? String.valueOf(loan.getReviewTime()).replace("T", " ") : "<span class='text-muted fst-italic'>尚未審核</span>" %></td>

                    <td class="text-center text-success"><%= loan.getApprovedRate() == null ? "-" : loan.getApprovedRate().multiply(new java.math.BigDecimal("100")).setScale(2, java.math.RoundingMode.HALF_UP) + "%" %></td>

                    <td class="text-center"><span class="badge bg-light text-dark border"><%= (loan.getReviewerId() != null) ? "ID: " + loan.getReviewerId() : "-" %></span></td>

                    <td class="text-center"><span class="badge <%= badgeClass %>"><%= statusText %></span></td>

                    <td class="text-center pe-3">
                        <% if ("PENDING".equals(loan.getStatus())) { %>
                        <div class="d-flex justify-content-center align-items-center gap-2">
                            <input type="number" id="reviewer_<%= loan.getApplicationId() %>" class="form-control form-control-sm reviewer-input" value="1" title="審核行員 ID">

                            <button class="btn btn-sm btn-outline-primary" style="font-size:12px; padding: 2px 8px;" onclick="openModal('<%= loan.getApplicationId() %>', '<%= loan.getApplyType() %>')">
                                <i class="bi bi-pencil-square"></i>
                            </button>

                            <form action="${pageContext.request.contextPath}/LoanApply" method="post" class="m-0">
                                <input type="hidden" name="filterStatus" value="<%= selectedStatus != null ? selectedStatus : "" %>">
                                <input type="hidden" name="action" value="approveDirect">
                                <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
                                <input type="hidden" name="applyType" value="<%= loan.getApplyType() != null ? loan.getApplyType() : "" %>">
                                <input type="hidden" name="approvedAmount" value="<%= loan.getApplyAmount() %>">
                                <input type="hidden" name="approvedPeriod" value="<%= loan.getApplyPeriod() %>">
                                <input type="hidden" name="reviewerId">
                                <button type="submit" class="btn btn-sm btn-success" style="font-size:12px; padding: 2px 8px;" onclick="this.form.reviewerId.value=document.getElementById('reviewer_<%= loan.getApplicationId() %>').value" title="快速核准">
                                    <i class="bi bi-check-lg"></i>
                                </button>
                            </form>

                            <form action="${pageContext.request.contextPath}/LoanApply" method="post" class="m-0">
                                <input type="hidden" name="filterStatus" value="<%= selectedStatus != null ? selectedStatus : "" %>">
                                <input type="hidden" name="action" value="rejectByBank">
                                <input type="hidden" name="applicationId" value="<%= loan.getApplicationId() %>">
                                <input type="hidden" name="reviewerId">
                                <button type="submit" class="btn btn-sm btn-outline-danger" style="font-size:12px; padding: 2px 8px;" onclick="this.form.reviewerId.value=document.getElementById('reviewer_<%= loan.getApplicationId() %>').value" title="拒絕申請">
                                    <i class="bi bi-x-lg"></i>
                                </button>
                            </form>
                        </div>
                        <% } else { %>
                        <span class="text-muted fst-italic">-</span>
                        <% } %>
                    </td>
                </tr>
                <%  }
                } else { %>
                <tr>
                    <td colspan="14" class="text-center py-5 text-muted">
                        <i class="bi bi-inbox fs-2 d-block mb-2"></i>目前沒有符合條件的案件資料
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="approveModal" tabindex="-1">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-light">
                <h5 class="modal-title fs-6"><i class="bi bi-pencil-square me-2 text-primary"></i>修改貸款方案</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
                <input type="hidden" id="modal_applicationId">
                <input type="hidden" id="modal_applyType">
                <div class="mb-3">
                    <label class="form-label text-muted small fw-bold text-uppercase">新方案金額 ($)</label>
                    <input type="number" id="modal_amount" class="form-control form-control-sm" min="1" placeholder="請輸入核准金額" required>
                </div>
                <div class="mb-2">
                    <label class="form-label text-muted small fw-bold text-uppercase">新方案期數</label>
                    <select id="modal_period" class="form-select form-select-sm"></select>
                </div>
            </div>
            <div class="modal-footer p-3 bg-light border-top-0">
                <button type="button" class="btn btn-sm btn-outline-secondary px-3" data-bs-dismiss="modal">取消</button>
                <button type="button" class="btn btn-sm btn-primary px-3" onclick="submitApprove()">確認修改</button>
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
    var myModal = null;
    document.addEventListener("DOMContentLoaded", function() {
        myModal = new bootstrap.Modal(document.getElementById('approveModal'));
    });

    // 查詢過濾
    function doFilter() {
        var status = document.getElementById("statusFilter").value;
        var minAmt = document.getElementById("minAmount").value;
        var maxAmt = document.getElementById("maxAmount").value;
        var params = [];
        if (status) params.push("status=" + status);
        if (minAmt) params.push("minAmount=" + minAmt);
        if (maxAmt) params.push("maxAmount=" + maxAmt);

        Swal.fire({ title: '資料撈取中...', allowOutsideClick: false, showConfirmButton: false, didOpen: () => { Swal.showLoading(); }});
        location.href = "LoanApply" + (params.length > 0 ? "?" + params.join("&") : "");
    }

    // 刪除按鈕
    function confirmDeleteRejected() {
        Swal.fire({
            title: '清除已拒絕案件？',
            text: "這將會永久刪除畫面上所有狀態為「已拒絕」的資料，此操作無法復原！",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            confirmButtonText: '是的，清除！',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                document.getElementById('deleteRejectedForm').submit();
            }
        });
    }

    // 打開 Modal
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

    // 送出 Modal 修改
    function submitApprove() {
        var applicationId = document.getElementById("modal_applicationId").value;
        var applyType = document.getElementById("modal_applyType").value;
        var amount = document.getElementById("modal_amount").value;
        var period = document.getElementById("modal_period").value;
        var reviewerId = document.getElementById("reviewer_" + applicationId).value;

        if (!amount || parseInt(amount) <= 0) {
            Swal.fire({ icon: 'error', title: '格式錯誤', text: '請輸入有效的核准金額', timer: 2000, showConfirmButton: false });
            return;
        }

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

    // 表格標題排序邏輯
    var currentSortCol = -1; var currentSortAsc = true;
    function sortTable(colIndex) {
        var table = document.querySelector("table tbody");
        var rows = Array.from(table.querySelectorAll("tr"));
        if (rows.length <= 1) return;

        if (currentSortCol === colIndex) { currentSortAsc = !currentSortAsc; }
        else { currentSortCol = colIndex; currentSortAsc = true; }

        rows.sort(function(a, b) {
            var valA = a.cells[colIndex].textContent.trim().replace(/[$,% 期]/g, "");
            var valB = b.cells[colIndex].textContent.trim().replace(/[$,% 期]/g, "");
            var numA = parseFloat(valA); var numB = parseFloat(valB);

            if (!isNaN(numA) && !isNaN(numB)) return currentSortAsc ? numA - numB : numB - numA;
            return currentSortAsc ? valA.localeCompare(valB, "zh-TW") : -valA.localeCompare(valB, "zh-TW");
        });
        rows.forEach(function(row) { table.appendChild(row); });

        document.querySelectorAll("th .arrow").forEach(function(el) { el.textContent = ""; });
        var arrows = document.querySelectorAll("th.sortable .arrow");
        if (arrows[colIndex]) { arrows[colIndex].innerHTML = currentSortAsc ? "<i class='bi bi-caret-up-fill small'></i>" : "<i class='bi bi-caret-down-fill small'></i>"; }
    }
</script>

</body>
</html>