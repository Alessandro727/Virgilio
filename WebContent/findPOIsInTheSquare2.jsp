<%@ page import="java.util.*"%>
<%@ page import="model.Venue"%>
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
	List<Venue> venuesInTheSquare = (List<Venue>)request.getAttribute("venuesInTheSquare");
	Venue startVenue = (Venue)request.getAttribute("startVenue");
	Venue endVenue = (Venue)request.getAttribute("endVenue");
	LatLngSquare llSquare = (LatLngSquare)request.getAttribute("llSquare");
	%>

<!-- script for Google Maps API -->
<!-- sensor=true for using gps sensor on PC -->
<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
<script type="text/javascript">
		
		var rectangle;
		var hidden = new Boolean();
		var map;
		
		function initialize() {
			
			// Map visualization options
			var mapOptions = {
					mapTypeId: google.maps.MapTypeId.ROADMAP,	//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
					mapTypeControl: false
			};
			
			
			// THE MAP
			map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
			var bounds = new google.maps.LatLngBounds();
			
			
			
			//	markers = [
			//		[latitude, longitude, 'name'],
			//   	[latitude, longitude, name],
			//    	[........, ........., ....]
			// 	]
			var markers = new Array();	//for markers
			var pos;
			var element;
			
			<%	for (Venue v: venuesInTheSquare) { %>
					pos = new google.maps.LatLng(<%= v.getLatitude() %>, <%= v.getLongitude() %>);
					
					element = new Array(pos, "<%= v.getName_fq() %>", "<%= v.getCategory_fq() %>", <%= v.getCheckinsNumber() %>);
					markers.push(element);
					
					bounds.extend(pos);
			<%	} %>
			
			//start position & end position
			var startPos = new google.maps.LatLng(<%= startVenue.getLatitude() %>, <%= startVenue.getLongitude() %>);
			var endPos = new google.maps.LatLng(<%= endVenue.getLatitude() %>, <%= endVenue.getLongitude() %>);
			
			bounds.extend(startPos);
			bounds.extend(endPos);
			
			map.fitBounds(bounds);
			
					
			
			var marker;
						
			// Start Marker & End Marker definition
			marker = new google.maps.Marker({
	            position: startPos,
	            map: map,
	            title: "<%= startVenue.getName_fq() %>"
	         });
			marker = new google.maps.Marker({
	            position: endPos,
	            map: map,
	            title: "<%= endVenue.getName_fq() %>"
	         });
			
			
			// Markers definition
			var infowindow = new google.maps.InfoWindow();
			var i;
			
			for (i=0; i<markers.length; i++) {				
		        marker = new google.maps.Marker({
		            position: markers[i][0],
		            map: map,
		            title: markers[i][1] + "\n" + markers[i][2] + "\n" + markers[i][3] + " checkins here",
		            //icon: 'https://chart.googleapis.com/chart?chst=d_map_pin_letter_withshadow&chld='+ (i+1) +'|FF776B|000000'
		            icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
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
			
			
			// draw the square area
			rectangle = new google.maps.Rectangle({
				strokeColor: '#FF0000',
			    strokeOpacity: 0.8,
			    strokeWeight: 2,
			    fillColor: '#FF0000',
			    fillOpacity: 0.35,
			    map: map,
			    bounds: new google.maps.LatLngBounds(
			    	new google.maps.LatLng(<%= llSquare.getMinLat() %>, <%= llSquare.getMinLng() %>),
			    	new google.maps.LatLng(<%= llSquare.getMaxLat() %>, <%= llSquare.getMaxLng() %>
	))
		});

		hidden = false;

	} // end initialize function

	google.maps.event.addDomListener(window, 'load', initialize); // equivale a scrivere <body onload="initialize()">

	function showOrHiddenArea() {
		if (hidden) {
			rectangle.setMap(map);
			hidden = false;
			document.getElementById('btnSquare').value = 'Hidden area';
		} else {
			rectangle.setMap(null);
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
	<br />
	<div>
		<b><%=venuesInTheSquare.size()%></b> POIs in the square
	</div>
	<div id="googleMap"
		style="width: 700px; height: 560px; position: relative; top: 0; overflow: hidden;"></div>
	<div>
		<input id="btnSquare" type="button" value="Hidden area"
			onclick="showOrHiddenArea()">
	</div>

</body>
</html>