<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 12/04/2020
  Time: 10:32 SA
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User view</title>
</head>
<body>
<center>
    <h1>User view</h1>
    <h2>
        <a href="/users">List all user</a>
    </h2>
</center>
<div align="center">
    <table border="1" cellpadding="5">
        <caption><h2>User information</h2></caption>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Country</th>
        </tr>
        <tr>
            <td><c:out value="${user.id}"/></td>
            <td><c:out value="${user.name}"/></td>
            <td><c:out value="${user.email}"/></td>
            <td><c:out value="${user.country}"/></td>
        </tr>
    </table>
</div>
</body>
</html>
