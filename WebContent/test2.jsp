<%@ page import="java.util.*" %>
<%@ page import="model.Venue" %>
<%@ page import="model.Scenario" %>
<%@ page import="model.Context" %>
<%@ page import="logic.router.Route" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

	<%	
	@SuppressWarnings("unchecked")
	List<Route> severalRoutes = (List<Route>)request.getAttribute("severalRoutes");
	Venue startVenue = severalRoutes.get(0).getNode(0).getVenue();
	Venue endVenue = severalRoutes.get(0).getNode(severalRoutes.get(0).getSize()-1).getVenue();
	String mode = (String)request.getAttribute("mode");
	Scenario scenario = (Scenario)request.getAttribute("scenario");
	
	String food = "";
	if (!scenario.getFood())
		food = "non "; 
	String scen = "";
	scen += "Sono le ore " + scenario.getHour() + " di " + scenario.getDay() + "\\n\\n";
	scen += "Il tempo è " + scenario.getContext().getMeteo() + "\\n\\n";
	scen += "Hai a disposizione " + (scenario.getContext().getTime()/60) + " ore di tempo libero \\n\\n";
	scen += "Sei " + scenario.getContext().getMode().replace("driving", "in auto").replace("walking", "a piedi") + "\\n\\n";
	scen += "Ti trovi a " + scenario.getContext().getCity() + ", e vuoi andare da " + scenario.getContext().getStart() + " a " + scenario.getContext().getEnd() + "\\n\\n";
	scen += "Hai pensato di " + food + "fare una sosta per mangiare";
	%>
	
	<!-- script for Google Maps API -->
	<!-- sensor=true for using gps sensor on PC -->
	<script src="http://maps.google.com/maps/api/js?sensor=false&language=it"></script>
	
	<!-- script for map construction -->
	<script type="text/javascript">
		
		var map;
		var severalRoutesJS = new Array();
		var directionsRenderer = new google.maps.DirectionsRenderer();		
		var directionsService = new google.maps.DirectionsService();
		var travelMode;
		var index;
		
				
		function initialize() {
			
			var routeJS;
			<%	for (Route route: severalRoutes) { %>
					routeJS = new Array();		
					<%	for (Venue v: route.getVenueList()) { %>
							routeJS.push(new Array(	"<%= v.getName_fq() %>",
													"<%= v.getCategory_fq() %>",
													'<%= v.getLatitude() %>,<%= v.getLongitude() %>',
													<%= v.getMacro_category().getMrt() %>,
													<% if(v.getFoursquare_id() != null) { %>
														'<%= "<a href=\"https://foursquare.com/v/" + v.getFoursquare_id() + "\" target=\"_blank\">Details</a>"  %>')												
													<% } else { %>
														'No details available')
													<% } %>
										);
					<%	} %>
					severalRoutesJS.push(routeJS);
			<%	} %>			
			
			
			
			
			<% if (mode.equals("driving")) { %>
				travelMode = google.maps.DirectionsTravelMode.DRIVING;
    		<% } else { %>
    			travelMode = google.maps.DirectionsTravelMode.WALKING;
    		<% } %>
			
			
			
			for(var i=0; i<severalRoutesJS.length; i++) {
				var option = document.createElement("option");
				option.text = " route n° " + (i+1);
				document.getElementById("ddlRoutes").add(option);
			}
			
			
			// Map visualization options
			var mapOptions = {
					mapTypeId: google.maps.MapTypeId.ROADMAP,	//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
					mapTypeControl: false
			};
			
			
			// THE MAP
			map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
			
			directionsRenderer.setMap(map);
			
			calcRoute(0);
			
									
		}	// end initialize function
		
		
		function calcRoute(k) {
	        var waypts = [];
	        
	        var routeJS = severalRoutesJS[k];
	        
	        for(var i=1; i<routeJS.length-1; i++) {
	        	waypts.push({
        			location: routeJS[i][2],
        			stopover: true});
	        }
	        
	        var request = {
			        origin: '<%= startVenue.getLatitude() + "," + startVenue.getLongitude()  %>', 
			        destination: '<%= endVenue.getLatitude() + "," + endVenue.getLongitude()  %>',
			        waypoints: waypts,
			        optimizeWaypoints: true,
			        travelMode: travelMode
			        };	       
	        
	        index = k;
	        directionsService.route(request, renderDirections);
		}
		
		
		
		function renderDirections(response, status) {
			if (status == google.maps.DirectionsStatus.OK) {
				var k = index;
				var routeJS = severalRoutesJS[k];
				directionsRenderer.setDirections(response);
				var route = response.routes[0];	// indicazioni sul percorso
				
				var j, jSucc;	// waypoint_order index
				route.legs[0].start_address = '<b>Start - ' + routeJS[0][0] +  '</b></br>' + route.legs[0].start_address;
				j = route.waypoint_order[0] + 1;
				route.legs[0].end_address = '<b>' + routeJS[j][0] + '</b> (' + routeJS[j][1] + ')';
				
				for (var i=0; i<route.waypoint_order.length; i++) {
					j = route.waypoint_order[i] + 1;
					route.legs[i+1].start_address = '<b>' + routeJS[j][0] + '</b><br>'
													+ '(' + routeJS[j][1] + ')</br></br>'
													+ routeJS[j][4]  + '<br>'
													+ route.legs[i+1].start_address;
					if (i < route.waypoint_order.length-1) {
						jSucc = route.waypoint_order[i+1] + 1;					
						route.legs[i+1].end_address = '<b>' + routeJS[jSucc][0] + '</b> (' + routeJS[jSucc][1] + ')'; // route.legs[i+1].end_address + ')';
					}
				}
				route.legs[route.legs.length-1].end_address = '<b>Arrive - ' + routeJS[routeJS.length-1][0] + '</b></br>' + route.legs[route.legs.length-1].end_address;	
				
				var summaryPanel = '';
				var letter = 'A';
				var totKm = 0;
				var totTime = 0;
				var indexOf;
				for (i=0; i<route.legs.length; i++) {							
					summaryPanel += '<b><div style="background-color:#CCFFFF">Route Segment: ' + letter + ' - ' + String.fromCharCode(letter.charCodeAt() + 1) + '</div></b>';
					indexOf = route.legs[i].start_address.indexOf("</br>");
					summaryPanel += 'From: &nbsp;&nbsp; ' + route.legs[i].start_address.substring(0, indexOf).replace("<br>", " ") + '<br>';
					summaryPanel += 'To: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ' + route.legs[i].end_address + '<br>';
					summaryPanel += 'Distance = ' + route.legs[i].distance.text + '<br>';
					summaryPanel += 'Travel Time = ' + route.legs[i].duration.text +'<br>';
					summaryPanel += 'Stop = ' + routeJS[i+1][3] + ' min<br><br>';
					letter = String.fromCharCode(letter.charCodeAt() + 1);
					totKm += route.legs[i].distance.value;
					totTime += (route.legs[i].duration.value);	// in seconds
					totTime += (routeJS[i][3])*60;		// mrt in seconds
				}
				summaryPanel += 'Total distance: ' + (totKm/1000) + ' km <br>';				
				summaryPanel += 'Total time: ' + Math.floor(totTime/3660) + ' h ';
				summaryPanel += Math.floor((totTime/60)%60) + ' min<br>';
				document.getElementById('summaryPanel').innerHTML = summaryPanel;
			}
			else {
				alert("Problema nella ricerca del percorso: " + status);
			}
			
		}
				
		
		google.maps.event.addDomListener(window, 'load', initialize);	// equivale a scrivere <body onload="initialize()">
		
		
		function ddlChanged() {
			var k = document.getElementById("ddlRoutes").selectedIndex;
			calcRoute(k);			
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
			<td><input id="btnScenario" type="button" value="Rivedi Scenario" onclick="window.alert('<%= scen %>')"></td>
			<td><select id="ddlRoutes" onchange="ddlChanged()"></select></td>
		</tr>
		<tr>
			<td valign="top"><br/><div id="googleMap" style="width:800px; height:700px; position:relative; top:0; overflow:hidden;"></div></td>
			<td valign="top"><br/><div id="summaryPanel" style="font-size:15"></div></td>
		</tr>
	</table>	

</body>
</html>