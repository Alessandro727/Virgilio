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
<script src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
	<script type="text/javascript">
	
		var map;
		var hidden = new Boolean();
		//var directionsService;
		var directionsRender;
		
		function initialize() {
			
			var latLngRome = new google.maps.LatLng(41.915563, 12.48436);
			
			// Map visualization options
			var mapOptions = {
					zoom: 11,
					center: latLngRome,
					mapTypeId: google.maps.MapTypeId.ROADMAP	//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
			};
			
			// THE MAP
			map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
			
			directionsService = new google.maps.DirectionsService();
			directionsRender = new google.maps.DirectionsRenderer();
		}
	
		
		function calcRoute() {
			
		    var request = {
		        origin: document.getElementById("txtStart").value, 
		        destination: document.getElementById("txtDestination").value,
		        travelMode: google.maps.DirectionsTravelMode.DRIVING
		    };		    		    
		    
		    directionsService.route(request, function(response, status) {
		      if (status == google.maps.DirectionsStatus.OK) {
 					var mapOptions = {
 						zoom:8,
 		    			mapTypeId: google.maps.MapTypeId.ROADMAP
 		    		};		  	
		  		    map = new google.maps.Map(document.getElementById("googleMap"), mapOptions);
		  		    directionsRender.setMap(map);
		  		    directionsRender.setDirections(response);
					// var route = response.routes[0];	<-- indicazioni sul percorso
		      }
		      else {
		        	alert("Problema nella ricerca del percorso: " + status);
		        }
		    });
		    
		    
		    hidden = false;
		    
		  }
				
		
		google.maps.event.addDomListener(window, 'load', initialize);	// equivale a scrivere <body onload="initialize()">
		
		function showOrHiddenArea() {
			if (hidden) {
				directionsRender.setMap(map);
				hidden = false;
				document.getElementById('btnSquare').value = 'Hidden area';
			} else {
				directionsRender.setMap(null);
				hidden = true;
				document.getElementById('btnSquare').value = 'Show area';
			}
		}
		
	</script>
	
</head>
<body>

	<div>
		<jsp:include page="menu.jsp" />
	</div>
	<br/>
	
	<table>
		<tr>
			<td>
				<div id="googleMap" style="width:700px; height:500px; position:relative; top:0; overflow:hidden;"></div>
			</td>
			<td>
				<div>Geocoding example</div>
				<br/>
				<div>Geocoding is the service for converting between an address and a geographical coordinate</div>
				<br/><br/>
				Start <input id="txtStart" type="text">
				<br/>
				Destination <input id="txtDestination" type="text">
				<br/>
				<input id="btnSearch" type="button" value="Calculate route" onclick="calcRoute()">
				<br/><br/>
				<div><input id="btnSquare" type="button" value="Hidden area" onclick="showOrHiddenArea()"></div>
			</td>
		</tr>
	</table>

</body>
</html>