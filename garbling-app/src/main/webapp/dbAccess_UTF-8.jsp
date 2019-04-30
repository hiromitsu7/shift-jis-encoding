<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="">
<head>
<meta charset="UTF-8">
<title>DBアクセス</title>
</head>
<body>

<form action="MyServlet" method="post">
  <input type="text" name="text"></input>
  <input type="submit"></input>
</form>

<table border="1">
  <c:forEach var="item" items="${list}" varStatus="status">
    <tr><td>${status.count}</td><td>${item}</td></tr>
  </c:forEach>
</table>

</body>
</html>