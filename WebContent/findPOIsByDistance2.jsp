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
	List<Venue> venues = (List<Venue>) request.getAttribute("venues");
	Venue addressedVenue = (Venue) request.getAttribute("addressedVenue");
%>

<!-- script for Google Maps API -->
<!-- sensor=true for using gps sensor on PC -->
<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
<script type="text/javascript">
				
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
			var pos;
			var element;
			
			<%for (Venue v : venues) {%>
					pos = new google.maps.LatLng(<%=v.getLatitude()%>, <%=v.getLongitude()%>);
					
					element = new Array(pos, "<%=v.getName_fq().replace("\"", "\\\"")%>", "<%=v.getCategory_fq()%>");
					markers.push(element);
					
					bounds.extend(pos);
			<%}%>
			
			//addressed position
			var addressedPos = new google.maps.LatLng(<%=addressedVenue.getLatitude()%>, <%=addressedVenue.getLongitude()%>);
			
			bounds.extend(addressedPos);
			map.fitBounds(bounds);
			
					
			
			var marker;
						
			// Addressed Marker definition
			marker = new google.maps.Marker({
	            position: addressedPos,
	            map: map,
	            title: "<%=addressedVenue.getName_fq()%>" 
	            // escamotage: name_fq contains the formatted address
		});

		// Markers definition
		var infowindow = new google.maps.InfoWindow();
		var i;

		for (i = 0; i < markers.length; i++) {
			marker = new google.maps.Marker({
				position : markers[i][0],
				map : map,
				title : markers[i][1] + "\n" + markers[i][2],
				//icon: 'https://chart.googleapis.com/chart?chst=d_map_pin_letter_withshadow&chld='+ (i+1) +'|FF776B|000000'
				icon : 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
			});

			google.maps.event.addListener(marker, 'click', function(marker, i) {
				return function() {
					//infowindow.setContent(venues[i][2]);
					infowindow.open(map, marker);
				};
			}(marker, i));

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
	<div><%=venues.size()%>
		POI trovati
	</div>
	<br />

	<div id="googleMap"
		style="width: 700px; height: 500px; position: relative; top: 0; overflow: hidden;"></div>

</body>
</html>