<%@ page import="java.util.*"%>
<%@ page import="model.Checkin"%>
<%@ page import="model.Venue"%>
<%@ page import="postgres.CheckinPostgres"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<%	int checkinID = Integer.parseInt(request.getParameter("checkin_id"));
		List<Checkin> checkins = CheckinPostgres.RetrieveCheckinsByCheckinID(checkinID);
	%>

<!-- script for Google Maps API -->
<!-- sensor=true for using gps sensor on PC -->
<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
<script type="text/javascript">
		var map;
		
		
		function initialize() {
			
			// Map visualization options
			var mapOptions = {
					mapTypeId: google.maps.MapTypeId.ROADMAP,	//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
					mapTypeControl: false
			};
			
			
			// THE MAP
			var map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
			var bounds = new google.maps.LatLngBounds();
			
			//	markers = [
			//		[latitude, longitude, 'name'],
			//   	[latitude, longitude, name],
			//    	[........, ........., ....]
			// 	]
			var markers = new Array();	//for markers
			var venues = new Array();	// for polyline
			<%	Venue v;
				for (Checkin c: checkins) { 
					v = c.getVenue();
			%>
					var element = new Array(<%= v.getLatitude() %>, <%= v.getLongitude() %>, "<%= v.getName_fq() %>");
					markers.push(element);
					
					var pos = new google.maps.LatLng(<%= v.getLatitude() %>, <%= v.getLongitude() %>);
					venues.push(pos);
					bounds.extend(pos);
			<%	} %>
			map.fitBounds(bounds);
			
					
			
			
			// Marker definition
			var infowindow = new google.maps.InfoWindow();
			var marker;
			var i;
			
			for (i=0; i<markers.length; i++) {				
		        marker = new google.maps.Marker({
		            position: venues[i],
		            map: map,
		            title: (i+1) + ": " + markers[i][2],
		            icon: 'https://chart.googleapis.com/chart?chst=d_map_pin_letter_withshadow&chld='+ (i+1) +'|FF776B|000000'
		        });
				
				google.maps.event.addListener(
						marker,
						'click',
						function(marker, i) {
							return function() {
		                		//infowindow.setContent(venues[i][2]);
		                		infowindow.open(map, marker);
		            		};
						}(marker, i)
				);			
				
			}
			
						
				
			
			// Polyline definition
			var trajectory = new google.maps.Polyline({
				path: venues,
				map: map,
				strokeColor: "blue"
			});
		}		
		
		google.maps.event.addDomListener(window, 'load', initialize);	// equivale a scrivere <body onload="initialize()">
		
	</script>

</head>
<body>

	<div>
		<jsp:include page="menu.jsp" />
	</div>
	<br />

	<table border="1">
		<tr>
			<th>n°</th>
			<th>Date of Checkin</th>
			<th>Latitude</th>
			<th>Longitude</th>
			<th>Name</th>
			<th>Category</th>
		</tr>
		<%	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int i = 1;
		for(Checkin c: checkins) { %>
		<tr>
			<td><%= i %></td>
			<td><%= df.format(c.getDate()) %></td>
			<td><%= c.getVenue().getLatitude() %></td>
			<td><%= c.getVenue().getLongitude() %></td>
			<td><%= c.getVenue().getName_fq() %></td>
			<td><%= c.getVenue().getCategory_fq() %></td>
		</tr>
		<% 	i++;
		} %>
	</table>

	<br />
	<div id="googleMap"
		style="width: 700px; height: 500px; position: relative; top: 0; overflow: hidden;"></div>

</body>
</html>