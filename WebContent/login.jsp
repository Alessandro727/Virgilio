<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html >
<head>
    <meta charset="UTF-8">
    <title>Virgilio</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

    <link rel="stylesheet" href="css/style.css">
</head>

<body background="background.jpg">
<hgroup>
    <h1>Virgilio</h1>
    <h3>By Alessandro Fogli</h3>
</hgroup>
<form id="form" action="Login" method="post">
    <div class="group">
        <input id="txtUsername" name="txtUsername" type="text" /><span class="highlight"></span><span class="bar"></span>
        <label>Username</label>
    </div>
    <div class="group">
        <input id="txtPassword" name="txtPassword" type="password" /><span class="highlight"></span><span class="bar"></span>
        <label>Password</label>
    </div>
    <button id="btnLogin" name="btnLogin" value="Login" type="submit" class="button buttonBlue">Login
        <div class="ripples buttonRipples"><span class="ripplesCircle"></span></div>
    </button>
    <button id="btnRegister" name="btnRegister" onclick="window.location='./register1.jsp'" type="button" class="button buttonBlue">Register
        <div class="ripples buttonRipples"><span class="ripplesCircle"></span></div>
    </button>

</form>



<script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>

<script  src="js/index.js"></script>

</body>
</html>
