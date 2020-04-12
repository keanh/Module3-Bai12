<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 12/04/2020
  Time: 11:11 SA
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Select User By Country</title>
</head>
<body>
<center>
    <h1>Select User By Country</h1>
    <h2>
        <a href="/users?action=users">List all users</a>
    </h2>
</center>
<div align="center">
    <form method="post">
        <div>Enter country: <input type="text" id="country" name="country" size="30"></div>
        <input type="submit" id="submit" name="submit" value="Search">

        <div align="center">
            <table border="1" cellpadding="5">
                <caption><h4>Select users by country</h4></caption>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Country</th>
                </tr>
                <c:forEach var="user" items="${country}">
                    <tr>
                        <td><c:out value="${user.id}"/></td>
                        <td><c:out value="${user.name}"/></td>
                        <td><c:out value="${user.email}"/></td>
                        <td><c:out value="${user.country}"/></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </form>
</div>
</body>
</html>
