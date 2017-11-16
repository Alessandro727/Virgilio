<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html >
<head>
    <meta charset="UTF-8">
    <title>Virgilio</title>



    <link rel="stylesheet" href="css/style.css">


</head>

<body background="background.jpg">
<hgroup>
    <h1>Virgilio</h1>
    <h3>By Alessandro Fogli</h3>
</hgroup>
<form action="Login" method="post">
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
    <button id="btnRegister" name="btnRegister" onclick="register()" class="button buttonBlue">Register
        <div class="ripples buttonRipples"><span class="ripplesCircle"></span></div>
    </button>



</form>

<script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>

<script  src="js/index.js"></script>

<script>
    function register() {
        window.open("./register1.jsp")
    }

</script>

</body>
</html>
