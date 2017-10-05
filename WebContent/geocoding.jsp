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
	function initialize() {

		var latLngRome = new google.maps.LatLng(41.915563, 12.48436);

		// Map visualization options
		var mapOptions = {
			zoom : 11,
			center : latLngRome,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
		};

		// THE MAP
		var map = new google.maps.Map(document.getElementById('googleMap'),
				mapOptions);
	}

	function createMap(address) {

		var geocoder = new google.maps.Geocoder();

		geocoder.geocode({
			'address' : address
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var mapOptions = {
					zoom : 13,
					center : results[0].geometry.location,
					mapTypeId : google.maps.MapTypeId.ROADMAP
				};
				var map = new google.maps.Map(document
						.getElementById('googleMap'), mapOptions);
				var marker = new google.maps.Marker({
					position : results[0].geometry.location,
					map : map
				});
			} else {
				alert("Problema nella ricerca dell'indirizzo: " + status);
			}
		});
	}

	function searchAddress() {
		var address = document.getElementById("txtAddress").value;
		if (address) //check for not null string
			createMap(address);
		else
			alert("Insert an address");
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
				<div>Geocoding example</div> <br />
				<div>Geocoding is the service for converting between an
					address and a geographical coordinate</div> <br />
			<br /> <input id="txtAddress" type="text"> <input
				id="btnSearch" type="button" value="Search address on the map"
				onclick="searchAddress()">
			</td>
		</tr>
	</table>

</body>
</html>