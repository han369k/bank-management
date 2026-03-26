<%@ include file="/WEB-INF/views/navbar.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>信用卡列表</title>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<h2>信用卡列表</h2>

<p>
    <a href="${pageContext.request.contextPath}/card?action=new">新增信用卡</a>
</p>

<c:choose>
<c:when test="${not empty cardList}">
<table border="1" style="padding:8px;">
    <tr>
        <th>卡片ID</th>
        <th>客戶ID</th>
        <th>卡別</th>
        <th>卡號</th>
        <th>到期日</th>
        <th>狀態</th>
        <!-- <th>更新status</th> -->
        <th>修改</th>
        <th>刪除</th>
    </tr>
<c:forEach var="card" items="${cardList}">
    <tr>
        <form action="${pageContext.request.contextPath}/card" method="post" class="card-edit-form">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="cardId" value="${card.cardId}">

            <td>${card.cardId}</td>
            
            <td>
                <input type="text" name="customerId" value="${card.customerId}" class="edit-field" disabled>
            </td>

            <td>
                <input type="text" name="cardTypeId" value="${card.cardTypeId}" class="edit-field" disabled>
            </td>

            <td>
                **** **** **** ${fn:substring(card.cardNumber, fn:length(card.cardNumber)-4, fn:length(card.cardNumber))}
                <input type="hidden" name="cardNumber" value="${card.cardNumber}">
            </td>

            <td>
                <input type="date" name="expiryDate" value="${card.expiryDate}" class="edit-field" disabled>
            </td>

            <td>
                <select name="cardStatus" class="edit-field" disabled>
                    <option value="ACTIVE" ${card.status == 'ACTIVE' ? 'selected' : ''}>有效</option>
                    <option value="INACTIVE" ${card.status == 'INACTIVE' ? 'selected' : ''}>尚未啟用</option>
                    <option value="BLOCKED" ${card.status == 'BLOCKED' ? 'selected' : ''}>已停用</option>
                </select>
            </td>

            <td>
                <button type="button" class="edit-btn">修改</button>
                <button type="submit" class="save-btn" style="display:none;">儲存</button>
            </td>
        </form>

        <td>
            <form action="${pageContext.request.contextPath}/card" method="post">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="cardId" value="${card.cardId}">
                <button type="button" class="delete-btn">刪除</button>
            </form>
        </td>
    </tr>
</c:forEach>


</table>
</c:when>

<c:otherwise>
    <p>目前沒有資料</p>
</c:otherwise>
</c:choose>

<br>
<a href="${pageContext.request.contextPath}/creditCardHome">回首頁</a>

<script>
// 必須先初始化 Toast
const Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 2000,
    timerProgressBar: true
});

// 修改按鈕：解鎖該列的輸入框
document.querySelectorAll('.edit-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        const tr = this.closest('tr');
        // 找出該列所有 class 為 edit-field 的東西 (包含 expiryDate)
        tr.querySelectorAll('.edit-field').forEach(el => {
            el.disabled = false; // 解鎖！
        });
        this.style.display = 'none'; // 隱藏自己
        tr.querySelector('.save-btn').style.display = 'inline'; // 顯示儲存
    });
});

// 儲存表單送出：確保解鎖並跳通知
document.querySelectorAll('.card-edit-form').forEach(form => {
    form.addEventListener('submit', function (e) {
        // 先暫停送出，為了做最後檢查與跳通知
        e.preventDefault(); 

        // 重要：再次確保所有 edit-field 是啟用狀態，否則後端 getParameter 會拿到 null
        this.querySelectorAll('.edit-field').forEach(el => {
            el.disabled = false;
        });

        Toast.fire({
            icon: 'info',
            title: '正在儲存資料...'
        });

        // 延遲 0.5 秒再正式送出，讓使用者看得到通知
        setTimeout(() => {
            this.submit(); 
        }, 500);
    });
});

// 刪除確認
document.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        const form = this.closest('form');
        Swal.fire({
            title: '確定刪除？',
            text: "刪除後資料無法復原！",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            confirmButtonText: '確定刪除',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                form.submit();
            }
        });
    });
});

// 提示訊息處理 
const successMsg = "${msg}";
if (successMsg && successMsg !== "null" && successMsg.trim() !== "") {
    Toast.fire({ icon: 'success', title: successMsg });
}

const errorMsg = "${errorMsg}";
if (errorMsg && errorMsg !== "null" && errorMsg.trim() !== "") {
    Toast.fire({ icon: 'error', title: errorMsg });
}

</script>

</body>
</html>