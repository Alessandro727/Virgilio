<!--	<a href="login.jsp">Login</a>
	| <a href="register1.jsp">Register</a>
	| <a href="test1.jsp">Test</a>
	| <a href="findCheckinByDate1.jsp">Find Checkins by date</a>
	| <a href="findTopKPopularRoutes1.jsp">Find Top K Popular route</a>
	| <a href="findPOIsInTheSquare1.jsp">Find POIs in the square</a>
	| <a href="findPOIsByDistance1.jsp">Find POIs by distance</a>
	| <a href="createGraph.jsp">Create Graph</a>
	| <a href="centeredMap.jsp">Simple Map</a>
	| <a href="centeredMapWithMarker.jsp">Marker</a>
	| <a href="centeredMapWithPolyline.jsp">Polyline</a>
	| <a href="geocoding.jsp">Geocoding</a>
	| <a href="directions.jsp">Directions</a>
	| <a href="mapWithRouteBoxer.jsp">Route Boxer</a> -->
	
	 
	 
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Title</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <style>
        #bar { position: absolute; width: 100%;
            z-index: 40;
        }
        #name {
            color: #FFFFFF;
        }
    </style>

</head>
<body>

<nav class="navbar navbar-inverse" id="bar">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" id="name">Virgilio</a>
        </div>
        <div class="collapse navbar-collapse" id="myNavbar">
            <ul class="nav navbar-nav">
                <li class="active"><a href="findTopKPopularRoutes1.jsp">Home</a></li>
                <li>
                    <a href="test1.jsp">Test </a>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="register1.jsp"><span class="glyphicon glyphicon-user"></span> Sign Up</a></li>
                <li><a href="login.jsp"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
            </ul>
        </div>
    </div>
</nav>


</body>
</html>
