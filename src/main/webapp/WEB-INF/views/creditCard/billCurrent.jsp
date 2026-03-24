<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <h2>當期帳單</h2>
    <!-- ✅ 客戶下拉 -->
<form id="customerForm" method="get" action="${pageContext.request.contextPath}/bill">

    <label>選擇客戶：</label>

    <select name="customerId" id="customerSelect">
        <option value="">全部</option>

        <c:forEach var="c" items="${customerList}">
            <option value="${c}"
                <c:if test="${customerId == c}">selected</c:if>>
                客戶 ${c}
            </option>
        </c:forEach>
    </select>

</form>

<br>

<br>
    <table border="1">
        <tr>
            <th>帳單月份</th>
            <th>總金額</th>
            <th>已繳</th>
            <th>剩餘</th>
            <th>操作</th>
        </tr>

        <c:forEach var="bill" items="${billList}">
        <tr>
            <!-- 月份 -->
            <td>${bill.billingMonth}</td>

            <!-- 金額 -->
            <td>${bill.totalAmount}</td>
            <td>${bill.paidAmount}</td>
            <td>${bill.remainingAmount}</td>

            <!-- 操作 -->
            <td>
                <c:choose>
                    <c:when test="${not empty customerId}">
                        <!-- 查看明細（改用 month） -->
                        <a href="${pageContext.request.contextPath}/bill?action=detail&month=${bill.billingMonth}&customerId=${customerId}">
                            <button>查看明細</button>
                        </a>
                    </c:when>
                </c:choose>

                <!-- 繳款（先給固定金額避免錯誤） -->
                <!-- <form action="${pageContext.request.contextPath}/bill" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="pay">
                    <input type="hidden" name="month" value="${bill.billingMonth}">
                    <input type="hidden" name="amount" value="1000">
                    <button type="submit">繳款</button>
                </form> -->
            </td>
        </tr>
        </c:forEach>
    </table>

    <br>
    <a href="${pageContext.request.contextPath}/creditCardHome">返回信用卡首頁</a>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const customerSelect = document.getElementById("customerSelect");
            const form = document.getElementById("customerForm");
            
            customerSelect.addEventListener("change", function() {
                form.submit();
            });
        });
    </script>


</body>
</html>