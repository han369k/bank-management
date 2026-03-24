<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>帳單明細</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

  <style>
    /* 這裡只保留此頁面專用的摘要盒與佈局微調 */
    .page-wrapper { max-width: 900px; margin: 32px auto; padding: 0 16px; }

    .summary-box {
      background: #fff;
      border-radius: 8px;
      padding: 16px 20px;
      margin-bottom: 20px;
      border: 1px solid #eee;
      box-shadow: 0 1px 4px rgba(0,0,0,.05);
    }
    .summary-box .label { color: #888; margin-right: 6px; font-size: 12px; }
    .summary-box .value { font-weight: 600; color: #1a1d23; font-size: 14px; }

    .filter-bar {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 4px rgba(0,0,0,.08);
      padding: 16px 24px;
      margin-bottom: 16px;
    }
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
  <div class="d-flex align-items-center gap-3 mb-3">
    <a href="${pageContext.request.contextPath}/bill" class="btn btn-sm btn-outline-secondary">
      <i class="bi bi-arrow-left me-1"></i>返回帳單列表
    </a>
    <h1 class="page-title mb-0"><i class="bi bi-file-text me-2"></i>帳單明細</h1>
  </div>

  <div class="summary-box d-flex flex-wrap gap-4">
    <div><span class="label">帳單月份</span><span class="value">${month}</span></div>
    <div><span class="label">總金額</span><span class="value">$ ${calculatedTotal}</span></div>
    <div><span class="label">已繳金額</span><span class="value">$ 0</span></div>
    <div><span class="label">剩餘應繳</span><span class="value text-danger">$ ${calculatedRemaining}</span></div>
  </div>

  <div class="filter-bar d-flex align-items-center gap-3">
    <form id="filterForm" method="get" action="${pageContext.request.contextPath}/bill" class="d-flex align-items-center gap-2 mb-0">
      <input type="hidden" name="action" value="detail">
      <input type="hidden" name="month" value="${month}">
      <input type="hidden" name="customerId" value="${customerId}">
      <label class="form-label mb-0">選擇卡片：</label>
      <select name="cardId" id="cardSelect" class="form-select form-select-sm" style="width:220px;">
        <option value="" <c:if test="${empty selectedCardId}">selected</c:if>>全部卡片</option>
        <c:forEach var="card" items="${cardList}">
          <option value="${card.cardId}" <c:if test="${selectedCardId eq card.cardId}">selected</c:if>>
            **** **** **** ${card.cardNumber.substring(card.cardNumber.length()-4)}
          </option>
        </c:forEach>
      </select>
    </form>
  </div>

  <div class="card card-box p-0">
    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead>
        <tr>
          <th>日期</th>
          <th>描述</th>
          <th class="text-end">金額</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="tx" items="${details}">
          <tr>
            <td class="font-monospace text-muted">${tx.txnDate}</td>
            <td>${tx.description}</td>
            <td class="text-end fw-bold ${tx.txnAmount > 0 ? 'text-danger' : 'text-success'}">
              $ ${tx.txnAmount}
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty details}">
          <tr>
            <td colspan="3" class="text-center text-muted py-5">
              <i class="bi bi-inbox d-block fs-3 mb-2"></i>沒有交易明細
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
  // 下拉選單自動提交
  document.getElementById('cardSelect').addEventListener('change', function () {
    document.getElementById('filterForm').submit();
  });
</script>
</body>
</html>