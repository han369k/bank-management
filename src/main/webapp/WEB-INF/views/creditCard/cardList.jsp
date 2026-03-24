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
    <td class="card-id">${card.cardId}</td>
    
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
        <button type="button" class="save-btn" style="display:none;" 
                onclick="submitUpdate(this, '${card.cardId}')">儲存</button>
    </td>
    
    <td>
        <button type="button" class="delete-btn" onclick="confirmDelete('${card.cardId}')">刪除</button>
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
// 1. 必須先初始化 Toast
const Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 2000,
    timerProgressBar: true
});

// 2. 修改按鈕：解鎖欄位並切換按鈕
document.querySelectorAll('.edit-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        const tr = this.closest('tr');
        // 僅解鎖該列的輸入框與下拉選單
        tr.querySelectorAll('.edit-field').forEach(el => {
            el.disabled = false;
        });
        // 切換顯示「儲存」按鈕
        this.style.display = 'none';
        tr.querySelector('.save-btn').style.display = 'inline';
    });
});

// 3. 儲存按鈕
document.querySelectorAll('.save-btn').forEach(btn => {
    btn.addEventListener('click', function (e) {
        const tr = this.closest('tr');
        const form = tr.querySelector('form');

        // 再次確認欄位已解鎖，否則後端收不到值
        tr.querySelectorAll('.edit-field').forEach(el => {
            el.disabled = false;
        });

        Toast.fire({
            icon: 'info',
            title: '正在儲存中...'
        });
        
        
    });
});

// 4. 刪除確認 
document.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', function (e) {
        e.preventDefault();
        const form = this.closest('form');

        Swal.fire({
            title: '確認刪除',
            text: '確定要刪除這張信用卡嗎？',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonText: '取消',
            confirmButtonText: '確定刪除'
        }).then(result => {
            if (result.isConfirmed) {
                form.submit();
            }
        });
    });
});

// 5. 提示訊息處理 
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