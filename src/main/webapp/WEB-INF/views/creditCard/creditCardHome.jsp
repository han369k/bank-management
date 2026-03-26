<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>信用卡管理系統</title>
<style>
    body {
        font-family: Arial;
        background-color: #f5f6fa;
        text-align: center;
    }

    h1 {
        margin-top: 40px;
    }

    .container {
        margin-top: 50px;
    }

    .card {
        display: inline-block;
        width: 220px;
        padding: 20px;
        margin: 20px;
        border-radius: 10px;
        background-color: white;
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        transition: 0.2s;
    }

    .card:hover {
        transform: translateY(-5px);
    }

    a {
        text-decoration: none;
        color: black;
        font-weight: bold;
    }

    .title {
        font-size: 18px;
        margin-bottom: 10px;
    }

    .desc {
        font-size: 14px;
        color: gray;
    }
</style>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<h1>信用卡管理系統</h1>
<div class="container" style="text-align:center">
    <div class="card">
        <div class="title">卡片管理</div>
        <div class="desc">新增/編輯/刪除信用卡</div>
        <br>
        <a href="${pageContext.request.contextPath}/card">進入</a>
    </div>
    <div class="card">
        <div class="title">帳單管理</div>
        <div class="desc">查看當期帳單/繳款</div>
        <br>
        <a href="${pageContext.request.contextPath}/bill">進入</a>
    </div>
    <div class="card">
    		<div class="title">申請信用卡</div>
    		<div class="desc">選擇卡種並加入購物車</div>
    		<br>
    		<a href="${pageContext.request.contextPath}/cardType?action=list">進入</a>
    </div>

    <div class="card">
    		<div class="title">申請購物車</div>
    		
    		<div class="desc">查看已選卡片並送出申請</div>
    		<br>
    		<a href="${pageContext.request.contextPath}/cardCart?action=view">進入</a>
    </div>

    </div>

<!-- <button onclick="showAlert()">點我</button> -->
<script>
// function showAlert() {
//     Swal.fire({
//         title: 'Hello!',
//         text: '這是一個 SweetAlert2 的示例',
//         icon: 'success',
//         confirmButtonText: '確定'
//     });
// }

</script>
</body>
</html>