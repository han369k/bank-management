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
    /* 這裡僅保留此頁面專屬的 Grid 排版與金額顏色邏輯 */
    .page-wrapper { max-width: 960px; margin: 32px auto; padding: 0 16px; }

    .summary-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
      gap: 12px;
      margin-bottom: 24px;
    }

    .summary-item {
      background: #fff;
      border-radius: 8px;
      padding: 14px 16px;
      border: 1px solid #eee;
      box-shadow: 0 1px 4px rgba(0,0,0,.05);
    }
    .summary-item .s-label { font-size: 11px; color: #888; text-transform: uppercase; letter-spacing: 0.5px; }
    .summary-item .s-value { font-size: 16px; font-weight: 600; color: #1a1d23; margin-top: 4px; }

    /* 金額正負色調改用 style.css 規範 */
    .amount-positive { color: #c0392b; font-weight: 600; } /* 消費（支出） */
    .amount-negative { color: #0a7a3e; font-weight: 600; } /* 繳款（入帳） */

    .card-box { background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.08); padding: 24px; }
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
  <div class="d-flex align-items-center gap-3 mb-4">
    <a href="${pageContext.request.contextPath}/bill" class="btn btn-sm btn-outline-secondary">
      <i class="bi bi-arrow-left me-1"></i>返回帳單列表
    </a>
    <h1 class="page-title mb-0"><i class="bi bi-file-earmark-text me-2"></i>帳單明細</h1>
  </div>

  <div class="summary-grid">
    <div class="summary-item">
      <div class="s-label">帳單 ID</div>
      <div class="s-value font-monospace">${bill.billId}</div>
    </div>
    <div class="summary-item">
      <div class="s-label">卡片 ID</div>
      <div class="s-value font-monospace">${bill.cardId}</div>
    </div>
    <div class="summary-item">
      <div class="s-label">帳單月份</div>
      <div class="s-value">${bill.billingMonth}</div>
    </div>
    <div class="summary-item">
      <div class="s-label">總金額</div>
      <div class="s-value">$ ${bill.totalAmount}</div>
    </div>
    <div class="summary-item">
      <div class="s-label">已繳</div>
      <div class="s-value text-success">$ ${bill.paidAmount}</div>
    </div>
    <div class="summary-item">
      <div class="s-label">剩餘應繳</div>
      <div class="s-value text-danger fw-bold">$ ${bill.totalAmount - bill.paidAmount}</div>
    </div>
  </div>

  <div class="card-box">
    <h6 class="fw-semibold mb-3"><i class="bi bi-list-ul me-1"></i>交易明細清單</h6>
    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead>
        <tr>
          <th>交易 ID</th>
          <th>商家 ID</th>
          <th class="text-end">金額</th>
          <th>類型</th>
          <th>時間</th>
          <th>備註</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="txn" items="${details}">
          <tr>
            <td class="font-monospace text-muted">${txn.txnId}</td>
            <td>${txn.merchantId}</td>
            <td class="text-end">
              <c:choose>
                <c:when test="${txn.txnAmount < 0}">
                  <span class="amount-negative">${txn.txnAmount}</span>
                </c:when>
                <c:otherwise>
                  <span class="amount-positive">+${txn.txnAmount}</span>
                </c:otherwise>
              </c:choose>
            </td>
            <td><span class="small text-uppercase">${txn.txnType}</span></td>
            <td class="font-monospace">${txn.txnDate}</td>
            <td class="text-muted">${txn.description}</td>
          </tr>
        </c:forEach>
        <c:if test="${empty details}">
          <tr>
            <td colspan="6" class="text-center text-muted py-5">
              <i class="bi bi-inbox d-block fs-3 mb-2"></i>目前沒有相關交易紀錄
            </td>
          </tr>
        </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>