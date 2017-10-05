<%@ page import="model.User"%>
<%
	User user = (User) session.getAttribute("user");
	if (user == null) {
		out.clear();
		RequestDispatcher rd = application.getRequestDispatcher("/login.jsp");
		rd.forward(request, response);
		return;
	}
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Find Top K Popular Routes</title>

<!-- script for Google Maps API -->
<!-- sensor=true for using gps sensor on PC -->
<!--  	  

<script 
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc&callback=initMap"
	type="text/javascript"> 


<script src="https://maps.google.com/maps/api/js?libraries=places&"></script> -->

<!--  <script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>
<!-- script for map construction -->


<script src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>



<script type="text/javascript">
	var map;
	var initialLocation;
	var autocompleteStart, autocompleteEnd;
	var rome = new google.maps.LatLng(41.9100711, 12.5359979);
	var browserSupportFlag = new Boolean();

	function initialize() {

		// Map visualization options
		var mapOptions = {
			zoom : 11,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		//displays a normal street map (sennò c'è HYBRID, SATELLITE, o TERRAIN')
		};

		// THE MAP
		map = new google.maps.Map(document.getElementById('googleMap'),
				mapOptions);

		// Create the autocomplete object, restricting the search to geographical location types.
		autocompleteStart = new google.maps.places.Autocomplete((document
				.getElementById('txtStart')), {
			types : [ 'geocode' ]
		}
		/* The types of predictions to be returned. Four types are supported:
		 *   1) 'establishment' for businesses
		 *   2) 'geocode' for addresses;
		 *	3) '(regions)' for administrative regions
		 *	4) '(cities)' for localities
		 *  If nothing is specified, all types are returned
		 */
		);

		autocompleteEnd = new google.maps.places.Autocomplete((document
				.getElementById('txtEnd')), {
			types : [ 'geocode' ]
		});

		// Try HTML5 geolocation
		if (navigator.geolocation) {
			browserSupportFlag = true;
			navigator.geolocation.getCurrentPosition(function(position) {
				initialLocation = new google.maps.LatLng(
						position.coords.latitude, position.coords.longitude);

				autocompleteStart.setBounds(new google.maps.LatLngBounds(
						initialLocation, initialLocation));
				autocompleteEnd.setBounds(new google.maps.LatLngBounds(
						initialLocation, initialLocation));

				var infoWindow = new google.maps.InfoWindow({
					//map: map,
					position : initialLocation,
					content : 'You are here'
				});
				infoWindow.open(map);

				map.setCenter(initialLocation);
			}, function() {
				handleNoGeolocation(browserSupportFlag); //true
			});
		} else {
			// Browser doesn't support Geolocation
			browserSupportFlag = false;
			handleNoGeolocation(browserSupportFlag); //false
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

	google.maps.event.addDomListener(window, 'load', initialize); // equivale a scrivere <body onload="initialize()">
</script>

</head>
<body>

	<div>
		<jsp:include page="menu.jsp" />
	</div>
	<br />

	<form action="FindTopKPopularRoutes" method="get">
		<table>
			<tr>
				<td>
					<div id="googleMap"
						style="width: 700px; height: 500px; position: relative; top: 0; overflow: hidden;"></div>
				</td>
				<td><br /> <br />
					<table>
						<tr>
							<td colspan="2" style="font-size: 20px; color: #000099">
								Hello <span style="font-weight: bold; font-style: italic"><%=user.getUsername()%></span>
							</td>
						</tr>
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
							<td>Mode:</td>
							<td><select name="ddlMode">
									<option value="driving">Driving</option>
									<option value="walking">Walking</option>
							</select></td>
						</tr>
						<tr>
							<td>Max N° of Venues:</td>
							<td><input id="txtMaxWayPoints" name="txtMaxWayPoints"
								type="text" size="3"></td>
						</tr>
						<tr>
							<td>Available Time</td>
							<td><select name="ddlHours">
									<option value="0">0</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
							</select> Hours &nbsp;&nbsp; <select name="ddlMinutes"
								onFocus="enableDisableInput(false)">
									<option value="0">00</option>
									<option value="1">01</option>
									<option value="2">02</option>
									<option value="3">03</option>
									<option value="4">04</option>
									<option value="5">05</option>
									<option value="6">06</option>
									<option value="7">07</option>
									<option value="8">08</option>
									<option value="9">09</option>
									<option value="10">10</option>
									<option value="11">11</option>
									<option value="12">12</option>
									<option value="13">13</option>
									<option value="14">14</option>
									<option value="15">15</option>
									<option value="16">16</option>
									<option value="17">17</option>
									<option value="18">18</option>
									<option value="19">19</option>
									<option value="20">20</option>
									<option value="21">21</option>
									<option value="22">22</option>
									<option value="23">23</option>
									<option value="24">24</option>
									<option value="25">25</option>
									<option value="26">26</option>
									<option value="27">27</option>
									<option value="28">28</option>
									<option value="29">29</option>
									<option value="30">30</option>
									<option value="31">31</option>
									<option value="32">32</option>
									<option value="33">33</option>
									<option value="34">34</option>
									<option value="35">35</option>
									<option value="36">36</option>
									<option value="37">37</option>
									<option value="38">38</option>
									<option value="39">39</option>
									<option value="40">40</option>
									<option value="41">41</option>
									<option value="42">42</option>
									<option value="43">43</option>
									<option value="44">44</option>
									<option value="45">45</option>
									<option value="46">46</option>
									<option value="47">47</option>
									<option value="48">48</option>
									<option value="49">49</option>
									<option value="50">50</option>
									<option value="51">51</option>
									<option value="52">52</option>
									<option value="53">53</option>
									<option value="54">54</option>
									<option value="55">55</option>
									<option value="56">56</option>
									<option value="57">57</option>
									<option value="58">58</option>
									<option value="59">59</option>
							</select> Minutes</td>
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
								name="cbCategories" value="10" /> Shop &amp; Service <br>
								<input type="checkbox" name="cbCategories" value="11" />
								Athletics &amp; Sports <br> <!-- <input type="checkbox" name="cbCategories" value="12"/> Others -->
							</td>
						</tr>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="2"><input id="btnSearch" type="submit"
								value="Calculate route"></td>
						</tr>
					</table> <br /> <%
 	if (request.getAttribute("error") != null) {
 %>
					<div><%=request.getAttribute("error")%></div> <%
 	}
 %></td>
			</tr>
		</table>
	</form>

</body>
</html>