<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>信用卡列表</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

  <style>
    /* 這裡只保留此頁面專用的表格編輯模式樣式 */
    .page-wrapper { max-width: 1100px; margin: 32px auto; padding: 0 16px; }

    .card-box {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 1px 4px rgba(0,0,0,.08);
      padding: 24px;
      border: 1px solid #eee;
    }

    /* 編輯欄位切換效果 */
    .edit-field:disabled {
      background: transparent !important;
      border: none !important;
      box-shadow: none !important;
      padding: 0 !important;
      color: #1a1d23;
      cursor: default;
    }
    .edit-field {
      border: 1px solid #ddd;
      border-radius: 4px;
      padding: 4px 8px;
      font-size: 13px;
      width: 100%;
      transition: all 0.2s;
    }

    /* 覆蓋 style.css 預設的表格 Header 以對齊此頁面邏輯 */
    .table thead th { font-size: 12px !important; }

    /* 狀態標籤微調 */
    .badge-status-dot {
      display: inline-block;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      margin-right: 6px;
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
      <a class="nav-link active" href="${pageContext.request.contextPath}/card">信用卡</a>
      <a class="nav-link" href="${pageContext.request.contextPath}/bill">帳單</a>
    </div>
  </div>
</nav>

<div class="page-wrapper">
  <div class="d-flex align-items-center justify-content-between mb-4">
    <h1 class="page-title mb-0"><i class="bi bi-credit-card me-2"></i>信用卡管理</h1>
    <a href="${pageContext.request.contextPath}/card?action=new" class="btn btn-primary btn-sm">
      <i class="bi bi-plus-circle me-1"></i>新增信用卡
    </a>
  </div>

  <div class="card-box">
    <c:choose>
      <c:when test="${not empty cardList}">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead>
            <tr>
              <th style="width: 80px;">ID</th>
              <th style="width: 100px;">客戶ID</th>
              <th style="width: 100px;">卡別</th>
              <th>信用卡號</th>
              <th>到期日</th>
              <th style="width: 150px;">狀態</th>
              <th style="width: 180px;" class="text-center">操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="card" items="${cardList}">
              <tr>
                <td class="font-monospace text-muted">${card.cardId}</td>
                <td>
                  <input type="text" name="customerId" value="${card.customerId}" class="edit-field" disabled>
                </td>
                <td>
                  <input type="text" name="cardTypeId" value="${card.cardTypeId}" class="edit-field" disabled>
                </td>
                <td class="font-monospace">
                  <span class="text-muted">**** **** ****</span> ${fn:substring(card.cardNumber, fn:length(card.cardNumber)-4, fn:length(card.cardNumber))}
                  <input type="hidden" name="cardNumber" value="${card.cardNumber}">
                </td>
                <td>
                  <input type="date" name="expiryDate" value="${card.expiryDate}" class="edit-field" disabled>
                </td>
                <td>
                  <select name="cardStatus" class="form-select form-select-sm edit-field" disabled>
                    <option value="ACTIVE"   ${card.status == 'ACTIVE'   ? 'selected' : ''}>🟢 有效 (ACTIVE)</option>
                    <option value="INACTIVE" ${card.status == 'INACTIVE' ? 'selected' : ''}>🟡 尚未啟用 (INACTIVE)</option>
                    <option value="BLOCKED"  ${card.status == 'BLOCKED'  ? 'selected' : ''}>🔴 已停用 (BLOCKED)</option>
                  </select>
                </td>
                <td class="text-center">
                  <button type="button" class="btn btn-sm btn-outline-secondary edit-btn">
                    <i class="bi bi-pencil me-1"></i>修改
                  </button>
                  <button type="button" class="btn btn-sm btn-success save-btn" style="display:none;"
                          onclick="submitUpdate(this, '${card.cardId}')">
                    <i class="bi bi-check2 me-1"></i>儲存
                  </button>
                  <button type="button" class="btn btn-sm btn-outline-danger delete-btn ms-1"
                          onclick="confirmDelete('${card.cardId}')">
                    <i class="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>
      </c:when>
      <c:otherwise>
        <div class="text-center text-muted py-5">
          <i class="bi bi-inbox fs-1 d-block mb-3"></i>
          <p class="mb-0">目前沒有信用卡資料</p>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // 初始化 SweetAlert2 Toast
  const Toast = Swal.mixin({
    toast: true, position: 'top-end',
    showConfirmButton: false, timer: 2000, timerProgressBar: true
  });

  // 進入編輯模式
  document.querySelectorAll('.edit-btn').forEach(btn => {
    btn.addEventListener('click', function () {
      const tr = this.closest('tr');
      tr.querySelectorAll('.edit-field').forEach(el => el.disabled = false);
      this.style.display = 'none';
      tr.querySelector('.save-btn').style.display = 'inline-block';
      tr.classList.add('table-warning'); // 醒目提示正在編輯哪一列
    });
  });

  // 執行刪除確認
  function confirmDelete(id) {
    Swal.fire({
      title: '確定要刪除嗎？',
      text: "刪除後將無法恢復此卡片資料！",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#1a1d23',
      cancelButtonColor: '#d33',
      confirmButtonText: '確定刪除',
      cancelButtonText: '取消'
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = "${pageContext.request.contextPath}/card?action=delete&cardId=" + id;
      }
    })
  }

  // 執行更新
  function submitUpdate(btn, id) {
    const tr = btn.closest('tr');
    const customerId = tr.querySelector('input[name="customerId"]').value;
    const cardTypeId = tr.querySelector('input[name="cardTypeId"]').value;
    const cardNumber = tr.querySelector('input[name="cardNumber"]').value;
    const expiryDate = tr.querySelector('input[name="expiryDate"]').value;
    const cardStatus = tr.querySelector('select[name="cardStatus"]').value;

    // 這裡建議建立一個隱藏表單發送 POST，或使用 AJAX
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '${pageContext.request.contextPath}/card';

    const fields = {
      action: 'update',
      cardId: id,
      customerId: customerId,
      cardTypeId: cardTypeId,
      cardNumber: cardNumber,
      expiryDate: expiryDate,
      cardStatus: cardStatus
    };

    for (let key in fields) {
      const input = document.createElement('input');
      input.type = 'hidden';
      input.name = key;
      input.value = fields[key];
      form.appendChild(input);
    }

    document.body.appendChild(form);
    Toast.fire({ icon: 'info', title: '正在提交修改...' });
    setTimeout(() => form.submit(), 500);
  }

  // 顯示訊息
  const successMsg = "${msg}";
  if (successMsg && successMsg !== "null" && successMsg.trim() !== "") {
    Toast.fire({ icon: 'success', title: successMsg });
  }
</script>
</body>
</html>