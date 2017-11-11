<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registration Poll</title>

<script
	src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en&key=AIzaSyCM4ZZEHZuIsF-LfxbooRXcsA487D269cc"></script>


</head>
<body>

	<br />

	<form action="Register" method="post">
		<div
			style="width: 430px; align: center; margin-left: auto; margin-right: auto">
			<div style="background-color: #3366CC; font-size: 30px; color: white">Insert
				your information</div>
			<br>
			<% if (request.getAttribute("error") != null) { %>
			<div>
				<span style="color: red"><%= request.getAttribute("error") %></span>
			</div>
			<% } %>
			<div style="background-color: #CCFFFF">Username:</div>
			<div>
				<input id="txtUsername" name="txtUsername" type="text" />
			</div>
			<br>
			<br>

			<div style="background-color: #CCFFFF">Password:</div>
			<div>
				<input id="txtPassword" name="txtPassword" type="text" />
			</div>
			<br>
			<br>

			<div style="background-color: #CCFFFF">Gender:</div>
			<div>
				<input type="radio" name="rdGender" value="male">Male
				&nbsp;&nbsp; <input type="radio" name="rdGender" value="female">Female
			</div>
			<br>
			<br>

			<div style="background-color: #CCFFFF">Age:</div>
			<div>
				<input id="txtAge" name="txtAge" type="text" size="2" maxlength="2" />
			</div>
			<br>
			<br>

			<div style="background-color: #CCFFFF">Role:</div>
			<div>
				<select name="ddlRole">
					<option value="Student">Student</option>
					<option value="Teacher">Teacher</option>
					<option value="Employee">Employee</option>
					<option value="Freelancer">Freelancer</option>
					<option value="Currently Unoccupied">Currently Unoccupied
					</option>
				</select>
			</div>
			<br>
			<br>

			<div style="background-color: #CCFFFF">Residence:</div>
			
					<input id="autocomplete" name="residence"
						placeholder="Enter your address"
						onFocus="enableDisableInput(false)" type="text"></input>

			<br>
			<br>
			<div style="background-color: #CCFFFF">Assign a weight to each
				category, based on your preference:</div>
			<table>
				<tr>
					<td>Museum</td>
					<td><input size="2" maxlength="4" type="text" name="txtMuseum"
						placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Art Museum \n History Museum \n Museum \n Planetarium \n Science Museum');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>History &amp; Monuments</td>
					<td><input size="2" maxlength="4" type="text"
						name="txtHistory" placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Historic Site \n	Monument / Landmark \n Outdoor Sculpture \n Pedestrian Plazas');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>Church</td>
					<td><input size="2" maxlength="4" type="text" name="txtChurch"
						placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Church');return false;">Vedi</a></td>
				</tr>
				<tr>
					<td>Arts</td>
					<td><input size="2" maxlength="4" type="text" name="txtArts"
						placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Art Gallery \n Arts & Entertainment \n Performing Arts Venue');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>Entertainment</td>
					<td><input size="2" maxlength="4" type="text"
						name="txtEntertainment" placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Aquarium \n Arcade \n Baseball Stadium \n Basketball Stadium \n Bowling Alley \n Casino \n Circus \n Country Dance Club \n Cricket Ground \n Disc Golf \n Football Stadium \n General Entertainment \n Go Kart Track \n Hockey Arena \n Theater \n Jazz Club \n Laser Tag \n Mini Golf \n Multiplex \n Music Venue \n Opera House \n Piano Bar \n Pool Hall \n Public Art \n Racetrack \n Rock Club \n Roller Rink \n Salsa Club \n Soccer Stadium \n Stadium \n Street Art \n Tennis \n Theater \n Theme Park \n Theme Park Ride / Attraction \n Track Stadium \n Water Park \n Zoo');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>Outdoors &amp; Recreation</td>
					<td><input size="2" maxlength="4" type="text"
						name="txtOutdoors" placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Botanical Gardens \n Bridge \n Campground \n Castle \n Cemetery \n Field \n Garden \n Harbor / Marina \n Hot Spring \n Island \n Lake \n Lighthouse \n Mountain \n National Parks \n Palaces \n Park \n Playground \n Plaza \n River \n Rugby Pitches \n Scenic Lookout \n Sculpture Garden \n Stables \n Towns \n Trail \n Trees \n Villages \n Vineyard \n Volcano \n Well');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>Food</td>
					<td><input size="2" maxlength="4" type="text" name="txtFood"
						placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Acai House \n Restaurant \n BBQ Joint \n Bagel Shop \n Bakery \n Bar \n Bistro \n Breakfast Spot \n Bubble Tea Shop \n Buffet \n Burger Joint \n Burrito Place \n Cafeteria \n Café \n Churrascaria \n Coffee Shop \n Creperie \n Cupcake Shop \n Deli / Bodega \n Dessert Shop \n Diner \n Distillery \n Donut Shop \n Fish & Chips Shop \n Food \n Fried Chicken Joint \n Frozen Yogurt \n Gastropub \n Hot Dog Joint \n Ice Cream Shop \n Irish Pub \n Juice Bar \n Mac & Cheese Joint \n Meatball Place \n Meyhane \n Pastelaria \n Pie Shop \n Pizza Place \n Ramen / Noodle House \n Salad Place \n Sandwich Place \n Snack Place \n Soup Place \n Souvlaki Shop \n Steakhouse \n Taco Place \n Tea Room \n Winery \n Wings Joint');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>Nightlife Spot</td>
					<td><input size="2" maxlength="4" type="text"
						name="txtNightlife" placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Beach Bar \n Beer Garden \n Brewery \n Champagne Bar \n Cocktail Bar \n Dive Bar \n Hookah Bar \n Hotel Bar \n Karaoke Bar \n Lounge \n Nightlife Spot \n Other Nightlife \n Pub \n Sake Bar \n Speakeasy \n Sports Bar \n Strip Club \n Whisky Bar \n Wine Bar');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>Shop &amp; Service</td>
					<td><input size="2" maxlength="4" type="text" name="txtShop"
						placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Accessories Store \n Adult Boutique \n Antique Shop \n Arts & Crafts Store \n Automotive Shop \n Baby Store \n Big Box Store \n Bike Shop \n Board Shop \n Bookstore \n Boutique \n Bridal Shop \n Butcher \n Camera Store \n Candy Store \n Car Dealership \n Cheese Shop \n Chocolate Shop \n Christmas Market \n Climbing Gym \n Clothing Store \n Comic Shop \n Convenience Store \n Cosmetics Shop \n Credit Union \n Cycle Studio \n Daycare \n Department Store \n Discount Store \n Drugstore / Pharmacy \n Dry Cleaner \n Electronics Store \n EV Charging Station \n Fabric Shop \n Farmers Market \n Financial or Legal Service \n Fish Market \n Flea Market \n Flower Shop \n Food & Drink Shop \n Food Court \n Fruit & Vegetable Store \n Furniture / Home Store \n Gaming Cafe \n Garden Center \n Gift Shop \n Gourmet Shop \n Grocery Store \n Gun Shop \n Hardware Store \n Health Food Store \n Herbs & Spices Store \n Hobby Shop \n Hunting Supply \n Internet Cafe \n IT Services \n Jewelry Store \n Kids Store \n Laundromat \n Laundry Service \n Lingerie Store \n Liquor Store \n Locksmith \n Lottery Retailer \n Mall \n Marijuana Dispensary \n Market \n Martial Arts Dojo \n Massage Studio \n Men\'s Store \n Miscellaneous Shop \n Mobile Phone Shop \n Motorcycle Shop \n Music Store \n Nail Salon \n Newsstand \n Optical Shop \n Organic Grocery \n Paper / Office Supplies Store \n Photography Lab \n Print Shop \n Real Estate Office \n Record Shop \n Recording Studio \n Recycling Facility \n Salon / Barbershop \n Shipping Store \n Shoe Repair \n Shoe Store \n Shop & Service \n Smoke Shop \n Souvenir Shop \n Spa \n Sporting Goods Shop \n Storage Facility \n Supermarket \n Tailor Shop \n Tanning Salon \n Tattoo Parlor \n Thrift / Vintage Store \n Toy / Game Store \n Track \n Used Bookstore \n Video Game Store \n Video Store \n Warehouse Store \n Wine Shop \n Women\'s Store \n Yoga Studio');return false;">Vedi</a>
					</td>
				</tr>
				<tr>
					<td>Athletics &amp; Sports</td>
					<td><input size="2" maxlength="4" type="text"
						name="txtAthletics" placeholder="0.5"> <a href="#"
						onclick="javascript:alert('Apres Ski Bar \n Athletics & Sports \n Baseball Field \n Basketball Court \n Beach \n Golf Course \n Hockey Field \n Rafting Spots \n Rock Climbing Spot \n Skate Park \n Skating Rink \n Ski Area \n Ski Chairlift \n Ski Chalet \n Ski Lodge \n Ski Trail \n Soccer Field \n Surf Spot \n Tennis Court \n Volleyball Court');return false;">Vedi</a>
					</td>
				</tr>
			</table>

			<br>
			<br>
			<div style="text-align: right">
				<input id="btnRegister" name="btnRegister" value=" Register "
					style="font-size: 18px; color: blue" type="submit" />
			</div>
		</div>
	</form>

	<script>

function initialize() {

var input = document.getElementById('autocomplete');
var autocomplete = new google.maps.places.Autocomplete(input);
}

function enableDisableInput(value) {
	document.getElementById('btnRegister').disabled = value;
}

google.maps.event.addDomListener(window, 'load', initialize);

</script>

</body>
</html>