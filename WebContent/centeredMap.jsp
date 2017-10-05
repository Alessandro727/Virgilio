<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<!-- script for Google Maps API -->
<!-- sensor=true for using gps sensor on PC -->
<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
<script type="text/javascript">
	var latLngEngineering = new google.maps.LatLng(41.855514, 12.469691); //map center

	function initialize() {

		// Map visualization options
		var mapOptions = {
			zoom : 14,
			center : latLngEngineering,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
		};

		// THE MAP
		var map = new google.maps.Map(document.getElementById('googleMap'),
				mapOptions);

	}

	google.maps.event.addDomListener(window, 'load', initialize); // equivale a scrivere <body onload="initialize()">
</script>

</head>
<body>

	<div>
		<jsp:include page="menu.jsp" />
	</div>
	<br />

	<table>
		<tr>
			<td>
				<div id="googleMap"
					style="width: 700px; height: 500px; position: relative; top: 0; overflow: hidden;"></div>
			</td>
			<td>
				<div>A simple map, centered on Roma Tre</div>
			</td>
		</tr>
	</table>

</body>
</html>