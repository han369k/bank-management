<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ispan.bankmanagement.customer.Customer" %>
<!DOCTYPE html>
<html>
<head>
    <title>隱藏的資料橋樑</title>
</head>
<body>
<div id="message">
    <%= request.getAttribute("actionMessage") != null ? request.getAttribute("actionMessage") : "" %>
    <%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "" %>
</div>

<table>
    <tr>
        <th>ID</th><th>ID Number</th><th>Name</th><th>DOB</th><th>Phone</th><th>Email</th><th>Address</th><th>Status</th>
    </tr>
    <%
        // 接收 Controller 傳過來的 customerList
        List<Customer> list = (List<Customer>) request.getAttribute("customerList");
        if (list != null) {
            for (Customer c : list) {
    %>
    <tr>
        <td><%= c.getCustomerId() != null ? c.getCustomerId() : "" %></td>
        <td><%= c.getIdNumber() != null ? c.getIdNumber() : "" %></td>
        <td><%= c.getName() != null ? c.getName() : "" %></td>
        <td><%= c.getDateOfBirth() != null ? c.getDateOfBirth() : "" %></td>
        <td><%= c.getPhone() != null ? c.getPhone() : "" %></td>
        <td><%= c.getEmail() != null ? c.getEmail() : "" %></td>
        <td><%= c.getAddress() != null ? c.getAddress() : "" %></td>
        <td><%= c.getStatus() != null ? c.getStatus() : "Active" %></td>
    </tr>
    <%
            }
        }
    %>
</table>
</body>
</html>