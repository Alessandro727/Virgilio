<%@ page import="java.util.*"%>
<%@ page import="model.Venue"%>
<%@ page import="logic.router.Route"%>
<%@ page import="logic.LatLngSquare"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<%	
	@SuppressWarnings("unchecked")
	List<Route> topKroute = (List<Route>)request.getAttribute("topKroute");	
	List<Venue> route = topKroute.get(0).getVenueList();
	Venue startVenue = route.get(0);
	Venue endVenue = route.get(route.size()-1);
	String mode = (String)request.getAttribute("mode");
	%>

<!-- script for Google Maps API -->
<!-- sensor=true for using gps sensor on PC -->
<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
<script type="text/javascript">
		
var map;
var routeJS = new Array();
var directionsRender;
var directionsService = new google.maps.DirectionsService();
var travelMode;

function initialize() {
	
	<%	for (Venue v: route) { %>	
		routeJS.push(new Array(	"<%= v.getName_fq() %>",
								'<%= v.getLatitude() %>,<%= v.getLongitude() %>',
								<%= v.getMacro_category().getMrt() %>)
					);
	<%	} %>
	
	
	<% if (mode.equals("driving")) { %>
		travelMode = google.maps.DirectionsTravelMode.DRIVING;
	<% } else { %>
		travelMode = google.maps.DirectionsTravelMode.WALKING;
	<% } %>
	
	
	// Map visualization options
	var mapOptions = {
			mapTypeId: google.maps.MapTypeId.ROADMAP,	//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
			mapTypeControl: false
	};
	
	
	// THE MAP
	map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
	
	calcRoute(); 
							
}	// end initialize function


function calcRoute() {
    var waypts = [];
    
    for(var i=1; i<routeJS.length-1; i++) {
    	waypts.push({
			location: routeJS[i][1],
			stopover: true});
    }
    
    var request = {
	        origin: '<%= startVenue.getLatitude() + "," + startVenue.getLongitude()  %>', 
	        destination: '<%= endVenue.getLatitude() + "," + endVenue.getLongitude()  %>',
			waypoints : waypts,
			optimizeWaypoints : true,
			travelMode : travelMode
		};

		directionsService.route(request, renderDirections);
	}

	function renderDirections(response, status) {
		if (status == google.maps.DirectionsStatus.OK) {
			directionsRender = new google.maps.DirectionsRenderer();
			directionsRender.setMap(map);
			directionsRender.setDirections(response);
			var route = response.routes[0]; // indicazioni sul percorso

			var j, jSucc; // waypoint_order index
			route.legs[0].start_address = '<b>Start</b></br>'
					+ route.legs[0].start_address;
			j = route.waypoint_order[0] + 1;
			route.legs[0].end_address = '<b>' + routeJS[j][0] + '</b> ('
					+ route.legs[0].end_address + ')';

			for (var i = 0; i < route.waypoint_order.length; i++) {
				j = route.waypoint_order[i] + 1;
				route.legs[i + 1].start_address = '<b>' + routeJS[j][0]
						+ '</b></br>' + route.legs[i + 1].start_address;
				if (i < route.waypoint_order.length - 1) {
					jSucc = route.waypoint_order[i + 1] + 1;
					route.legs[i + 1].end_address = '<b>' + routeJS[jSucc][0]
							+ '</b> (' + route.legs[i + 1].end_address + ')';
				}
			}
			route.legs[route.legs.length - 1].end_address = '<b>Arrive</b></br>'
					+ route.legs[route.legs.length - 1].end_address;

			var summaryPanel = '';
			var letter = 'A';
			var totKm = 0;
			var totTime = 0;
			for (i = 0; i < route.legs.length; i++) {
				summaryPanel += '<b>Route Segment: ' + letter + ' - '
						+ String.fromCharCode(letter.charCodeAt() + 1)
						+ '</b><br>';
				summaryPanel += 'FROM: &nbsp;&nbsp; '
						+ route.legs[i].start_address.replace("</br>", " (")
						+ ')<br>';
				summaryPanel += 'TO: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; '
						+ route.legs[i].end_address.replace("</br>", " (")
						+ ')<br>';
				summaryPanel += 'Distance = ' + route.legs[i].distance.text
						+ '<br>';
				summaryPanel += 'Time = ' + route.legs[i].duration.text
						+ '<br>';
				summaryPanel += 'Mean Residence Time = ' + routeJS[i][2]
						+ ' min<br><br>';
				letter = String.fromCharCode(letter.charCodeAt() + 1);
				totKm += route.legs[i].distance.value;
				totTime += route.legs[i].duration.value; // in seconds
				totTime += (routeJS[i][2]) * 60; // mrt in seconds					
			}
			summaryPanel += (totKm / 1000) + ' km <br>';
			summaryPanel += Math.floor(totTime / 3600) + ' h ';
			summaryPanel += Math.floor((totTime / 60) % 60) + ' min<br>';
			document.getElementById('summaryPanel').innerHTML = summaryPanel;
		} else {
			alert("Problema nella ricerca del percorso: " + status);
		}
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
			<td><div id="googleMap"
					style="width: 700px; height: 800px; position: relative; top: 0; overflow: hidden;"></div></td>
			<td><div id="summaryPanel"></div></td>
		</tr>
	</table>

</body>
</html>