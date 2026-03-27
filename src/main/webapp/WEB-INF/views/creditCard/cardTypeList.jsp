<%@ include file="/WEB-INF/views/navbar.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>卡片種類列表</title>

<style>
    body {
        font-family: Arial, sans-serif;
        padding: 20px;
    }

    h2 {
        margin-bottom: 10px;
    }

    a {
        text-decoration: none;
        color: #007bff;
    }

    a:hover {
        text-decoration: underline;
    }

    .btn {
        padding: 6px 12px;
        background-color: #28a745;
        color: white;
        border-radius: 5px;
        text-decoration: none;
    }

    .btn:hover {
        background-color: #218838;
    }

    button.btn {
    border: none;
    cursor: pointer;
}

    .table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 15px;
    }

    .table th, .table td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: center;
    }

    .table th {
        background-color: #f4f4f4;
    }

    .table tr:hover {
        background-color: #f9f9f9;
    }
    .btn:disabled {
    background-color: #aaa;
    cursor: not-allowed;
}
img {
    border-radius: 8px;
    transition: transform 0.2s;
}

img:hover {
    transform: scale(1.05);
}

</style>

</head>
<body>

    <h2>卡片種類列表</h2>

    <a href="${pageContext.request.contextPath}/cardCart?action=view">🛒 查看購物車</a>
    <hr>

    <table class="table">
        <tr>
            <th>ID</th>
            <th>卡別名稱</th>
            <th>品牌</th>
            <th>年費</th>
            <th>回饋率</th>
            <th>圖片</th>
            <th>操作</th>
        </tr>

        <c:forEach var="type" items="${cardTypeList}">
            <tr>
                <td>${type.cardTypeId}</td>
                <td>${type.cardTypeName}</td>
                <td>${type.brand}</td>
                <td>${type.annualFee}</td>
                <td>${type.cashbackRate}%</td>
                <td><img src="${pageContext.request.contextPath}${type.cardImageUrl}" alt="${type.cardTypeName}" width="100"></td>
                <td>
                    <!-- <a class="btn"
                       href="${pageContext.request.contextPath}/cardCart?action=add&cardTypeId=${type.cardTypeId}">
                        加入購物車
                    </a> -->
                    <form class="add-form" action="${pageContext.request.contextPath}/cardCart" method="post">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="cardTypeId" value="${type.cardTypeId}">
                        <button class="btn add-btn" type="submit">加入購物車</button>



                    </form>
                </td>
            </tr>
        </c:forEach>

    </table>
    <script>
    // 為所有加入購物車的表單添加事件監聽器
const forms = document.querySelectorAll('.add-form');
forms.forEach(form => { 
    form.addEventListener('submit', function(event) {
    event.preventDefault();

    const btn = this.querySelector('.add-btn');

    if (btn.disabled) return;

    btn.disabled = true;
    btn.textContent = '已加入購物車';

    fetch(this.action, {
        method: 'POST',
        body: new FormData(this)
    });
});
});

    </script>


</body>
</html>