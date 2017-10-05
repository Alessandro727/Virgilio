<%@ page import="java.util.*" %>
<%@ page import="postgres.ContextPostgres" %>
<%@ page import="model.Context" %>
<%@ page import="model.Scenario" %>
<%@ page import="model.User" %>

<%	Scenario scenario = null;
	Context context = null;
	User user = (User)session.getAttribute("user");
	if (user == null) {
		out.clear();
		RequestDispatcher rd = application.getRequestDispatcher("/login.jsp");
		rd.forward(request, response);
		return;
	} else {
		context = ContextPostgres.RetrieveRandomContext();
		scenario = new Scenario(user.getId(), context);
		session.setAttribute("scenario", scenario);
	}
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	
	<br><br><br>
	<form action="Test2" method="get">
		<div style="width:500px; font-size:20px; align:center; margin-left:auto; margin-right:auto">
			Supponi di trovarti nel seguente scenario <span style="font-size:15px">(<a href="test1.jsp">cambia scenario</a>)</span>:
		
			<ul type="disc">
		  		<li>Sono le ore <strong><%= scenario.getHour() %></strong> di <strong><%= scenario.getDay() %></strong></li>
		  		<li>Il tempo è <strong><%= context.getMeteo() %></strong></li>
		  		<li>Hai a disposizione <strong><%= context.getTime()/60 %> ore</strong> di tempo libero</li>
		  		<li>Sei <strong><%= context.getMode() %></strong></li>
		  		<li>Ti trovi a <strong><%= context.getCity() %></strong>, e vuoi andare da <strong><%= context.getStart() %></strong> a <strong><%= context.getEnd() %></strong></li>
		  		<li>Pensi di fare una sosta per mangiare? <input type="radio" name="rbFood" value="true">si &nbsp;<input type="radio" name="rbFood" value="false">no</li>
			</ul>
			
			<br>
			Ti saranno ora suggeriti diversi itinerari, ai quali dovresti attribuire un punteggio.
			<br><br>
			
			
				<input id="btnTest" name="btnTest" value=" Avanti >> " type="submit" />			
		</div>
	</form>

</body>
</html>