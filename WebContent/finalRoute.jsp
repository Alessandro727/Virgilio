<%@ page import="java.util.*"%>
<%@ page import="model.Venue"%>
<%@ page import="logic.router.Route"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Insert title here</title>

    <%
    @SuppressWarnings("unchecked")
    List<Route> topKroute = (List<Route>)session.getAttribute("topKroute");
    Venue startVenue = topKroute.get(0).getNode(0).getVenue();
    Venue endVenue = topKroute.get(0).getNode(topKroute.get(0).getSize()-1).getVenue();
    String mode = (String)session.getAttribute("mode");
    %>

    <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>

    <style>
        html {
            height: 100%
        }

        body {
            height: 100%;
            margin: 100%;
            padding: -100%
        }

        #container {
            width: 100%;
            height: 100%;
            top: 50px;
        }

        #googleMap {
            width: 100%;
            height: 100%;
            top: 50px;
            position: absolute;
        }

        #form {
            position: absolute;
            z-index: 90;
            width: 420px;
            height: 50%;
            left: 40px;
            top: 50px;
            left: 50px;
        }

        #background {
            position: absolute;
            z-index: 90;
            width: 456px;
            height: 100%;
            top: 54px;
            background-color: #F7F7F7;
            opacity: 0.5;
            background-color: #F7F7F7;
        }
         .image {
             width: 180px;
             height: 300px;
         }

        .boxed2 {
            width: 250px;
            height: 300px;
            vertical-align: middle;
            margin-left: auto;
            margin-right: auto;
            align: center;
        }
        
        #multimedia {
        		margin-top: 100px;
        		margin-left: 60px;
        }
        
    </style>
    <!-- script for Google Maps API -->
    <!-- sensor=true for using gps sensor on PC -->
    <!--  <script
        src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
    <!-- script for map construction -->
    </head>
<body id="body">
	<div>
		<jsp:include page="menu.jsp" />
	</div>
	<div id="container">


		<div id="background"></div>
		<form id="form" action="Routes" method="post" action="/routes.jsp">
			<p></p>
			<p></p>
			<p></p>
			<p></p>
			<p></p>
			<p></p>
			<table id="table">
				<tr>

					<td>
						
						<div id="summaryPanel"></div>
					</td>
				</tr>
			</table>
			<p></p>
			<p></p>
			<p></p>
			<p></p>
			<p></p>
			<p></p>
			
		</form>
		<div id="googleMap"></div>
	</div>
<div id= "multimedia">
<table>
    <tr>
        <td style="padding-right: 200px"><h5>Potresti essere interessato al seguente libro.</h5></td>
        <td style="padding-right: 150px"><h5>Se ti &egrave;  piaciuto l'itinerario potresti  voler guardare il seguente film.</h5></td>
        <td style="padding-right: 150px"><h5>Immergiti al massimo all'interno dei luoghi che stai visitando accompagnando l'itinerario con della buona musica.</h5></td>
    </tr>
    <tr>
        <td style="padding-right: 200px"><h5> &Egrave; ambientato nel posto che stai visitando.</h5></td>
        <td style="padding-right: 150px"><h5>&Egrave; stato girato nella stesa citt&agrave; che stai visitando.</h5></td>
        <td style="padding-right: 150px"><h5>Il cantante &egrave; nativo del posto. Ascolta qualche brano di <%=(String)session.getAttribute("singerName")%>. </h5></td>
    </tr>
</table>
<p></p>

<table>
    <tr>
        <td style="padding-right: 30px">

            <div></div>
            <div id="bookImage" class="message">
                <img src="<%=(String)session.getAttribute("linkBook")%>"
                class="image" id="bookimg" />
            </div>
        </td>
        <td class="boxed2" style="padding-right: 40px">
            <div>

                Leggi la descrizione e decidi se comprarlo al prezzo migliore
                cliccando sul seguente link.
            </div>

            <p><a id="linkBook" href="<%=(String)session.getAttribute("textBook")%>" style="white-space: pre-line" >Clicca Qui</a></p>

        </td>
        <td style="padding-right: 50px">


            <div id="movieImage" class="message">
                <img src="<%=(String)session.getAttribute("posterUrl")%>"
                class="image" id="poster" />
            </div>
        </td>
        <td class="boxed2" style="padding-right: 40px">
            <div>

                Puoi visualizzare il trailer, il cast e decidere se acquistarlo dal successivo link.
            </div>

            <p><a id="linkMovie" href="<%=(String)session.getAttribute("linkMovie")%>" style="white-space: pre-line" >Clicca Qui</a></p>

        </td>
        <td style="padding-right: 50px">


            <div id="singerImage" class="message">
                <img src="<%=(String)session.getAttribute("singerCover")%>"
                class="image" id="singer" />
            </div>
        </td>
        <td class="boxed2" style="padding-right: 40px">
            <div>

                Puoi ascoltare le hit dell'autore e decidere se acquistare il suo ultimo cd dal seguente link.
            </div>

            <p><a id="linkMovie" href="<%=(String)session.getAttribute("singerAlbum")%>" style="white-space: pre-line" >Clicca Qui</a></p>

        </td>
    </tr>
</table>

</div>


 <button style="margin:auto; display:block;" id="btnLogin" name="btnLogin" onclick="window.location='./evaluation.jsp'" type="button" class="button buttonBlue">Done
        <div class="ripples buttonRipples"><span class="ripplesCircle"></span></div> 
    </button>


</body>
<script
        src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>



<!-- script for map construction -->
<script type="text/javascript">

    var map;
    var topKrouteJS = new Array();
    var directionsRenderer = new google.maps.DirectionsRenderer();
    var directionsService = new google.maps.DirectionsService();
    var travelMode;
    var index;

    var colors = ['blue', 'red', 'green', 'orange', 'yellow'];

    function getColor() {
        var color = colors.shift();
        colors.push(color);
        return color;
    }



    function initialize() {

        var routeJS;
    <%	for (Route route: topKroute) { %>
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
    topKrouteJS.push(routeJS);
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

    directionsRenderer.setMap(map);

    calcRoute(0);


    }	// end initialize function


    function calcRoute(k) {
        var waypts = [];

        var routeJS = topKrouteJS[k];

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
            directionsRenderer.setDirections(response);
            var route = response.routes[0];	// indicazioni sul percorso

            var j, jSucc;	// waypoint_order index
            route.legs[0].start_address = '<b>Start</b></br>' + route.legs[0].start_address;
            j = route.waypoint_order[0] + 1;
            route.legs[0].end_address = '<b>' + topKrouteJS[k][j][0] + '</b> (' + topKrouteJS[k][j][1] + ')';

            for (var i=0; i<route.waypoint_order.length; i++) {
                j = route.waypoint_order[i] + 1;
                route.legs[i+1].start_address = '<b>' + topKrouteJS[k][j][0] + '</b><br>'
                    + '(' + topKrouteJS[k][j][1] + ')</br></br>'
                    + topKrouteJS[k][j][4]  + '<br>'
                    + route.legs[i+1].start_address;
                if (i < route.waypoint_order.length-1) {
                    jSucc = route.waypoint_order[i+1] + 1;
                    route.legs[i+1].end_address = '<b>' + topKrouteJS[k][jSucc][0] + '</b> (' + topKrouteJS[k][jSucc][1] + ')'; // route.legs[i+1].end_address + ')';
                }
            }
            route.legs[route.legs.length-1].end_address = '<b>Arrive</b></br>' + route.legs[route.legs.length-1].end_address;

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
                summaryPanel += 'Stop = ' + topKrouteJS[k][i+1][3] + ' min<br><br>';
                letter = String.fromCharCode(letter.charCodeAt() + 1);
                totKm += route.legs[i].distance.value;
                totTime += (route.legs[i].duration.value);	// in seconds
                totTime += (topKrouteJS[k][i][3])*60;		// mrt in seconds
            }
            summaryPanel += (totKm/1000) + ' km <br>';
            summaryPanel += Math.floor(totTime/3660) + ' h ';
            summaryPanel += Math.floor((totTime/60)%60) + ' min<br>';
            document.getElementById('summaryPanel').innerHTML = summaryPanel;
        }
        else {
            alert("Problema nella ricerca del percorso: " + status);
        }

        var result = $("#table").height();
        var result2 = $("#body").height();
        if(result>result2)	{
            $("#body").height(result+100);
            $("#form").height(result+50);
            $("#background").height(result+54);
            $("#googleMap").height(result+54);

            google.maps.event.trigger(map, 'resize');

        }


    }


    google.maps.event.addDomListener(window, 'load', initialize);	// equivale a scrivere <body onload="initialize()">


    

</script>


</html>