<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
</head>
<body>

	<jsp:include page="menu.jsp" />
	
	<br><br>
	
	<form action="Login" method="post">
		<table style="border:2px solid #003399; border-collapse:separate; font-family:Calibri" cellpadding="5" cellspacing="0"  width="260px">
			<tr style="padding-bottom:10px">
				<th colspan="2" align="left" bgcolor="#3366CC" style="font-size:30px; color:white">&nbsp;&nbsp;&nbsp;Log in</th>
			</tr>
			<tr>
				<td align="right">Username:</td>
				<td style="padding-top:15px" width="60%"><input id="txtUsername" name="txtUsername" type="text" /> </td>
			</tr>
			<tr>
				<td align="right">Password:</td>
				<td><input id="txtPassword" name="txtPassword" type="password" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input id="btnLogin" name="btnLogin" value="Login" type="submit" /></td>
			</tr>
			<tr>
				<td colspan="2"><hr style="border:1px solid #3366CC"></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td style="padding-bottom:10px"><input id="btnLogin" name="btnLogin" value="Login as a guest" type="submit" /></td>
			</tr>
		</table>
		
		<br/>
		<% if (request.getAttribute("error") != null) { %>
		<div><span style="color:red"><%= request.getAttribute("error") %></span></div>
		<% } %>
	</form>
</body>
</html>