<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>當期帳單</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

  <style>
    /* 這裡只保留此頁面專有的排版微調，字體與顏色已由 style.css 統一控管 */
    .page-wrapper { max-width: 900px; margin: 32px auto; padding: 0 16px; }
    .page-title { font-size: 20px; font-weight: 600; margin-bottom: 20px; }
    .card-box { background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.08); padding: 24px; }

    /* 列表標題顏色同步主色調 */
    .table thead th { background: #1a1d23; color: #fff; font-weight: 500; font-size: 13px; border: none; }
    .table tbody td { font-size: 13px; vertical-align: middle; }

    .filter-bar { background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.08); padding: 16px 24px; margin-bottom: 16px; }
    .form-label { font-size: 13px; font-weight: 500; margin-bottom: 4px; }
    .form-select { font-size: 13px; }
  </style>
</head>
<body>

<nav class="navbar navbar-expand-lg">
  <div class="container-fluid px-4">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/index.html">
      <i class="bi bi-bank me-2"></i>Bank Management
    </a>
    <div class="navbar-nav ms-3">
      <a class="nav-link" href="${pageContext.request.contextPath}/index.html">首頁</a>
      <a class="nav-link" href="${pageContext.request.contextPath}/card">信用卡</a>
      <a class="nav-link active" href="${pageContext.request.contextPath}/bill">帳單</a>
    </div>
  </div>
</nav>

<div class="page-wrapper">
  <h1 class="page-title"><i class="bi bi-receipt me-2"></i>當期帳單</h1>

  <div class="filter-bar d-flex align-items-center gap-3">
    <form id="customerForm" method="get" action="${pageContext.request.contextPath}/bill" class="d-flex align-items-center gap-2 mb-0">
      <label class="form-label mb-0">選擇客戶：</label>
      <select name="customerId" id="customerSelect" class="form-select form-select-sm" style="width:180px;">
        <option value="">全部</option>
        <c:forEach var="c" items="${customerList}">
          <option value="${c}" <c:if test="${customerId == c}">selected</c:if>>客戶 ${c}</option>
        </c:forEach>
      </select>
    </form>
  </div>

  <div class="card-box">
    <div class="table-responsive">
      <table class="table table-bordered table-hover align-middle mb-0">
        <thead>
        <tr>
          <th>帳單月份</th>
          <th>總金額</th>
          <th>已繳</th>
          <th>剩餘</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="bill" items="${billList}">
          <tr>
            <td class="font-monospace">${bill.billingMonth}</td>
            <td class="fw-bold">$ ${bill.totalAmount}</td>
            <td class="text-success">$ ${bill.paidAmount}</td>
            <td class="text-danger">$ ${bill.remainingAmount}</td>
            <td>
              <c:if test="${not empty customerId}">
                <a href="${pageContext.request.contextPath}/bill?action=detail&month=${bill.billingMonth}&customerId=${customerId}"
                   class="btn btn-sm btn-outline-secondary">
                  <i class="bi bi-file-text me-1"></i>查看明細
                </a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty billList}">
          <tr>
            <td colspan="5" class="text-center text-muted py-4">
              <i class="bi bi-inbox d-block fs-4 mb-1"></i>沒有帳單資料
            </td>
          </tr>
        </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // 自動提交表單
  document.getElementById('customerSelect').addEventListener('change', function () {
    document.getElementById('customerForm').submit();
  });
</script>
</body>
</html>