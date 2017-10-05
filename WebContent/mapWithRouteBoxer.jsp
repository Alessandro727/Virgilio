<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>


<!-- script for RouteBoxer -->
<script type="text/javascript" src="src/RouteBoxer.js"></script>

<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->
<script type="text/javascript">
	var latLngEngineering = new google.maps.LatLng(41.855514, 12.469691); //map center

	function initialize() {

		// Map visualization options
		var mapOptions = {
			zoom : 14,
			center : latLngEngineering,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
		};

		// THE MAP
		var map = new google.maps.Map(document.getElementById('googleMap'),
				mapOptions);

		directionService = new google.maps.DirectionsService();
		directionsRenderer = new google.maps.DirectionsRenderer({
			map : map
		});
	}

	google.maps.event.addDomListener(window, 'load', initialize); // equivale a scrivere <body onload="initialize()">

	function route() {

		// Clear any previous route boxes from the map
		//clearBoxes();

		// Convert the distance to box around the route from miles to km
		distance = parseFloat(document.getElementById("distance").value) * 1.609344;

		var request = {
			origin : document.getElementById("from").value,
			destination : document.getElementById("to").value,
			travelMode : google.maps.DirectionsTravelMode.DRIVING
		}

		// Make the directions request
		directionService.route(request, function(result, status) {
			if (status == google.maps.DirectionsStatus.OK) {
				directionsRenderer.setDirections(result);

				// Box around the overview path of the first route
				var path = result.routes[0].overview_path;
				var boxes = routeBoxer.box(path, distance);
				drawBoxes(boxes);
			} else {
				alert("Directions query failed: " + status);
			}
		});
	}

	// Draw the array of boxes as polylines on the map
	function drawBoxes(boxes) {
		boxpolys = new Array(boxes.length);
		for (var i = 0; i < boxes.length; i++) {
			boxpolys[i] = new google.maps.Rectangle({
				bounds : boxes[i],
				fillOpacity : 0,
				strokeOpacity : 1.0,
				strokeColor : '#000000',
				strokeWeight : 1,
				map : map
			});
		}
	}

	var RouteBox = function(path, range) {

		// Two dimensional array representing the cells in the grid overlaid on the path
		this.grid_ = null;

		// Array that holds the latitude coordinate of each vertical grid line
		this.latGrid_ = [];

		// Array that holds the longitude coordinate of each horizontal grid line  
		this.lngGrid_ = [];

		// Array of bounds that cover the whole route formed by merging cells that
		//  the route intersects first horizontally, and then vertically
		this.boxesX_ = [];

		// Array of bounds that cover the whole route formed by merging cells that
		//  the route intersects first vertically, and then horizontally
		this.boxesY_ = [];

		// The array of LatLngs representing the vertices of the path
		var vertices = null;

		// If necessary convert the path into an array of LatLng objects
		if (path instanceof Array) {
			// already an arry of LatLngs (eg. v3 overview_path)
			vertices = path;
		} else if (path instanceof google.maps.Polyline) {
			if (path.getPath) {
				// v3 Maps API Polyline object
				vertices = new Array(path.getPath().getLength());
				for (var i = 0; i < vertices.length; i++) {
					vertices[i] = path.getPath().getAt(i);
				}
			} else {
				// v2 Maps API Polyline object
				vertices = new Array(path.getVertexCount());
				for (var j = 0; j < vertices.length; j++) {
					vertices[j] = path.getVertex(j);
				}
			}
		}

		// Build the grid that is overlaid on the route
		this.buildGrid(vertices, range);

		// Identify the grid cells that the route intersects
		this.findIntersectingCells_(vertices);

		// Merge adjacent intersected grid cells (and their neighbours) into two sets
		//  of bounds, both of which cover them completely
		this.mergeIntersectingCells_();

		// Return the set of merged bounds that has the fewest elements
		return (this.boxesX_.length <= this.boxesY_.length ? this.boxesX_
				: this.boxesY_);

	};

	var buildGrid = function(vertices, range) {

		// Create a LatLngBounds object that contains the whole path
		var routeBounds = new google.maps.LatLngBounds();
		for (var i = 0; i < vertices.length; i++) {
			routeBounds.extend(vertices[i]);
		}

		// Find the center of the bounding box of the path
		var routeBoundsCenter = routeBounds.getCenter();

		// Starting from the center define grid lines outwards vertically until they
		// extend beyond the edge of the bounding box by more than one cell
		this.latGrid_.push(routeBoundsCenter.lat());

		// Add lines from the center out to the north
		this.latGrid_.push(routeBoundsCenter.rhumbDestinationPoint(0, range)
				.lat());
		for (i = 2; this.latGrid_[i - 2] < routeBounds.getNorthEast().lat(); i++) {
			this.latGrid_.push(routeBoundsCenter.rhumbDestinationPoint(0,
					range * i).lat());
		}

		// Add lines from the center out to the south
		for (i = 1; this.latGrid_[1] > routeBounds.getSouthWest().lat(); i++) {
			this.latGrid_.unshift(routeBoundsCenter.rhumbDestinationPoint(180,
					range * i).lat());
		}

		// Starting from the center define grid lines outwards horizontally until they
		//  extend beyond the edge of the bounding box by more than one cell
		this.lngGrid_.push(routeBoundsCenter.lng());

		// Add lines from the center out to the east
		this.lngGrid_.push(routeBoundsCenter.rhumbDestinationPoint(90, range)
				.lng());
		for (i = 2; this.lngGrid_[i - 2] < routeBounds.getNorthEast().lng(); i++) {
			this.lngGrid_.push(routeBoundsCenter.rhumbDestinationPoint(90,
					range * i).lng());
		}

		// Add lines from the center out to the west
		for (i = 1; this.lngGrid_[1] > routeBounds.getSouthWest().lng(); i++) {
			this.lngGrid_.unshift(routeBoundsCenter.rhumbDestinationPoint(270,
					range * i).lng());
		}

		// Create a two dimensional array representing this grid
		this.grid_ = new Array(this.lngGrid_.length);
		for (i = 0; i < this.grid_.length; i++) {
			this.grid_[i] = new Array(this.latGrid_.length);
		}

	};
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
					style="width: 700px; height: 500px; position: relative; top: 0; overflow: hidden;"></div>
			</td>
			<td>
				<div>A simple map, with a Route Boxer</div>
				<div>
					Box within at least <input type="text" id="distance" value="30"
						size="2">miles
				</div>
				<div>
					of the route from <input type="text" id="from" value="denver" />
				</div>
				<div>
					to <input type="text" id="to" value="dallas" />
				</div>
				<div>
					<input type="submit" onclick="route()" />
				</div>
			</td>
		</tr>
	</table>

</body>
</html>