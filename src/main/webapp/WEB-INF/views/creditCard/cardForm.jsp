<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新增信用卡</title>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 20px;
        background-color: #f4f4f4;
    }
    form {
        max-width: 400px;
        margin: auto;
    }
    label {
        display: block;
        margin-top: 10px;
    }
    input, select {
        width: 100%;
        padding: 8px;
        margin-top: 5px;
    }
    button:hover {
        margin-top: 15px;
        padding: 10px 20px;
        background-color: #0056b3;
    }
    button {
        margin-top: 15px;
        padding: 10px 20px;
        background-color: #007bff;
        color: white;
        border: none;
        cursor: pointer;
    }

</style>
</head>
<body>
    <h2>新增信用卡</h2>
    <form action="${pageContext.request.contextPath}/card" method="post">
        <input type="hidden" name="action" value="insert">
        <p>
            <label for="customerId">客戶ID:</label>
            <input type="text" id="customerId" name="customerId" required>
        </p>
        <p>
            <label for="cardTypeId">卡別:</label>
            <input type="text" id="cardTypeId" name="cardTypeId" required>
        </p>
        <p>
            <label for="cardNumber">卡號:</label>
            <input type="text" id="cardNumber" name="cardNumber" required>
        </p>
        <p>
            <label for="expiryDate">到期日:</label>
            <input type="date" id="expiryDate" name="expiryDate" required>
        </p>
        <p>
            <label for="cardStatus">狀態:</label>
            <select id="cardStatus" name="cardStatus" required>
                <option value="">請選擇狀態</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
                <option value="BLOCKED">BLOCKED</option>
            </select>
        </p>
        <button type="submit">新增</button>
        <a href="${pageContext.request.contextPath}/card?action=list">返回列表</a>
    </form>
</body>
</html>