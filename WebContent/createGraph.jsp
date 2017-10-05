<%@ page import="model.User"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<!DOCTYPE html>
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
		var autocompleteStart, autocompleteEnd;
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
			
			
			
			// Create the autocomplete object, restricting the search to geographical location types.
			autocompleteStart = new google.maps.places.Autocomplete(
			      (document.getElementById('txtStart')),			      
			      { types: ['geocode'] }
			       /* The types of predictions to be returned. Four types are supported:
			    	*   1) 'establishment' for businesses
			    	*   2) 'geocode' for addresses;
			      	*	3) '(regions)' for administrative regions
			      	*	4) '(cities)' for localities
			    	*  If nothing is specified, all types are returned
			    	*/
			);
			
			autocompleteEnd = new google.maps.places.Autocomplete(
				      (document.getElementById('txtEnd')),			      
				      { types: ['geocode'] }				       
			);					
			
			
			
			// Try HTML5 geolocation
			if(navigator.geolocation) {
				browserSupportFlag = true;
				navigator.geolocation.getCurrentPosition(function(position) {
					initialLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
					
					autocompleteStart.setBounds(new google.maps.LatLngBounds(initialLocation, initialLocation));
					autocompleteEnd.setBounds(new google.maps.LatLngBounds(initialLocation, initialLocation));
					
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
		
				
		
		function enableDisableInput(value) {
			document.getElementById('btnSearch').disabled = value;			
		}
		
		
			
		google.maps.event.addDomListener(window, 'load', initialize);	// equivale a scrivere <body onload="initialize()">
		
	</script>

</head>
<body>

	<div>
		<jsp:include page="menu.jsp" />
	</div>
	<br />

	<form action="CreateGraph" method="get">
		<table>
			<tr>
				<td>
					<div id="googleMap"
						style="width: 700px; height: 500px; position: relative; top: 0; overflow: hidden;"></div>
				</td>
				<td><br />
				<br />
					<table>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td>Start:</td>
							<td><input id="txtStart" name="txtStart"
								placeholder="Enter your address" size="45"
								onFocus="enableDisableInput(true)" type="text"></td>
						</tr>
						<tr>
							<td>Destination:</td>
							<td><input id="txtEnd" name="txtEnd"
								placeholder="Enter your address" size="45" type="text"></td>
						</tr>
						<tr>
							<td colspan="2"><br>
								<div>What categories are you interested?</div> <input
								type="checkbox" name="cbCategories" value="3" /> Museum <br>
								<input type="checkbox" name="cbCategories" value="8" /> History
								&amp; Monuments <br> <input type="checkbox"
								name="cbCategories" value="9" /> Church <br> <input
								type="checkbox" name="cbCategories" value="1" /> Arts <br>
								<input type="checkbox" name="cbCategories" value="2" />
								Entertainment <br> <input type="checkbox"
								name="cbCategories" value="7" /> Outdoors &amp; Recreation <br>
								<!-- <input type="checkbox" name="cbCategories" value="4"/> Event <br> -->
								<input type="checkbox" name="cbCategories" value="5" /> Food <br>
								<input type="checkbox" name="cbCategories" value="6" />
								Nightlife Spot <br> <input type="checkbox"
								name="cbCategories" value="10" /> Shop &amp; Service <br> <input
								type="checkbox" name="cbCategories" value="11" /> Athletics
								&amp; Sports <br> <!-- <input type="checkbox" name="cbCategories" value="12"/> Others -->
							</td>
						</tr>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="2"><input id="btnCreateGraph" type="submit"
								value="Create graph"></td>
						</tr>
					</table> <br /> <% if (request.getAttribute("error") != null) { %>
					<div><%= request.getAttribute("error") %></div> <% } %></td>
			</tr>
		</table>
	</form>

</body>
</html>