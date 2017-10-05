<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<!-- script for Google Maps API -->
<!-- sensor=true for using gps sensor on PC -->
<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
<script type="text/javascript">
	
		var map;
		var initialLocation;
		var rome = new google.maps.LatLng(41.9100711, 12.5359979);
		var browserSupportFlag =  new Boolean();
		
		
		function initialize() {
			
			// Map visualization options
			var mapOptions = {
				zoom: 11,
				mapTypeId: google.maps.MapTypeId.ROADMAP	//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
			};
			
			// THE MAP
			map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
			
			
			// Try HTML5 geolocation
			if(navigator.geolocation) {
				browserSupportFlag = true;
				navigator.geolocation.getCurrentPosition(function(position) {
					initialLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
					
					var infoWindow = new google.maps.InfoWindow({
						//map: map,
						position: initialLocation,
						content: 'You are here'
					});
					infoWindow.open(map);
					
					map.setCenter(initialLocation);
				}, function() {
					handleNoGeolocation(browserSupportFlag);	//true
				});
			} else {
				// Browser doesn't support Geolocation
				browserSupportFlag = false;
				handleNoGeolocation(browserSupportFlag);		//false
			}
			
		}
		
		
		
		function handleNoGeolocation(errorFlag) {
			if (errorFlag == true) {
				alert("Geolocation service failed.");
				initialLocation = rome;
			} else {
				alert("Your browser doesn't support geolocation. We've placed you in Rome.");
				initialLocation = rome;
		    }
		    map.setCenter(initialLocation);
		 }
		
		// oppure
		/* function handleNoGeolocation(errorFlag) {
			if (errorFlag) {
				var content = 'Error: The Geolocation service failed.';
			} else {
				var content = 'Error: Your browser doesn\'t support geolocation.';
			}
		
			var options = {
				map: map,
				position: rome,
				content: content
			};
		
			var infowindow = new google.maps.InfoWindow(options);
			map.setCenter(options.position);
		} */
	
		
		google.maps.event.addDomListener(window, 'load', initialize);	// equivale a scrivere <body onload="initialize()">
		
	</script>

</head>
<body>

	<div>
		<jsp:include page="menu.jsp" />
	</div>
	<br />

	<form action="FindPOIsInTheSquare" method="get">
		<table>
			<tr>
				<td>
					<div id="googleMap"
						style="width: 700px; height: 500px; position: relative; top: 0; overflow: hidden;"></div>
				</td>
				<td><br /> <br /> Start <input id="txtStart" name="txtStart"
					type="text"> <br /> Destination <input id="txtEnd"
					name="txtEnd" type="text"> <br /> <input id="btnSearch"
					type="submit" value="Find POIs"> <br /> <% if (request.getAttribute("error") != null) { %>
					<div><%= request.getAttribute("error") %></div> <% } %></td>
			</tr>
		</table>
	</form>

</body>
</html>