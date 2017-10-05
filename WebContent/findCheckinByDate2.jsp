<%@ page import="java.util.*" %>
<%@ page import="model.Checkin" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<table border="1">
		<tr>
			<th>Date of Checkins</th>
			<th>N° of checkins</th>
			<th></th>
		</tr>
	<%	@SuppressWarnings("unchecked")	
		List<Checkin> checkins = (List<Checkin>)request.getAttribute("checkins");
		for(Checkin c: checkins) { %>
			<tr>
				<td><%= c.getDateAsString() %></td>
				<td><%= c.getLocation_id() %></td>
				<td><a href="findCheckinByDate3.jsp?checkin_id=<%= c.getCheckin_id() %>">click</a></td>
			</tr>	
	<% 	} %>	
	</table>

</body>
</html>