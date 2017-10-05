<%@ page import="java.util.*"%>
<%@ page import="model.Venue"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<%
	@SuppressWarnings("unchecked")
	List<Venue> venuesInTheSquare = (List<Venue>) request.getAttribute("venuesInTheSquare");
	Venue startVenue = (Venue) request.getAttribute("startVenue");
	Venue endVenue = (Venue) request.getAttribute("endVenue");
%>

<head>
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
					zoom: 13,
					mapTypeId: google.maps.MapTypeId.ROADMAP	//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
			};
			
			// THE MAP
			var map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
			var bounds = new google.maps.LatLngBounds();
			
			
			var markers = new Array();	//for markers
			var pos;
			var element;
			
			<%for (Venue v : venuesInTheSquare) {%>
					pos = new google.maps.LatLng(<%=v.getLatitude()%>, <%=v.getLongitude()%>);
					
					element = new Array(pos, "<%=v.getName_fq()%>", "<%=v.getCategory_fq()%>");
					markers.push(element);
					
					bounds.extend(pos);
			<%}%>
			
			//start position & end position
			var startPos = new google.maps.LatLng(<%=startVenue.getLatitude()%>, <%=startVenue.getLongitude()%>);
			var endPos = new google.maps.LatLng(<%=endVenue.getLatitude()%>, <%=endVenue.getLongitude()%>);
			
			bounds.extend(startPos);
			bounds.extend(endPos);
			
			map.fitBounds(bounds);
			
			
			var marker;
			
			// Start Marker & End Marker definition
			marker = new google.maps.Marker({
	            position: startPos,
	            map: map,
	            title: "<%=startVenue.getName_fq()%>"
	         });
			marker = new google.maps.Marker({
	            position: endPos,
	            map: map,
	            title: "<%=endVenue.getName_fq()%>"
		});

		for (var i = 0; i < markers.length; i++) {
			marker = new google.maps.Marker({
				position : markers[i][0],
				map : map,
				icon : 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
			});
		}

		var trajectory;

		// archi usceni dal nodo di partenza
		for (i = 0; i < markers.length; i++) {
			trajectory = new google.maps.Polyline({
				path : [ startPos, markers[i][0] ],
				map : map,
				strokeWeight : 1,
				strokeColor : "blue"
			});
		}

		// archi usceni dal nodo di partenza
		for (i = 0; i < markers.length; i++) {
			for (var j = 0; j < markers.length; j++) {
				if (i != j)
					trajectory = new google.maps.Polyline({
						path : [ markers[i][0], markers[j][0] ],
						map : map,
						strokeWeight : 1,
						strokeColor : "blue"
					});
			}
		}

		// archi entranti al nodo di arrivo
		for (i = 0; i < markers.length; i++) {
			trajectory = new google.maps.Polyline({
				path : [ markers[i][0], endPos ],
				map : map,
				strokeWeight : 1,
				strokeColor : "blue"
			});
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
			<td>
				<div id="googleMap"
					style="width: 800px; height: 650px; position: relative; top: 0; overflow: hidden;"></div>
			</td>
			<!--<td>
				<div>A simple map, centered on Roma Tre, with the marker and a
					trajectory</div>
				<div>The path goes from Engineering to Literature, through San
					Paolo</div>
			</td> -->
		</tr>
	</table>

</body>
</html>