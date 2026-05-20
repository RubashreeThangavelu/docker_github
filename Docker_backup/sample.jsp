<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Simple JSP Test</title>
</head>
<body>

<h2>Welcome to JSP Test Page</h2>

<form method="post">
    Name: <input type="text" name="username" />
    <input type="submit" value="Submit" />
</form>

<%
    String name = request.getParameter("username");
    if (name != null && !name.isEmpty()) {
%>
        <p>Hello, <b><%= name %></b>! JSP is working fine ✅</p>
<%
    }
%>

</body>
</html>

