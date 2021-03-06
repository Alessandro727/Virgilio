package logic.router;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


import java.util.Map;
import java.util.Properties;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;

import logic.Filter;
import model.MacroCategory;
import model.Venue;
import postgres.CheckinPostgres;
import postgres.PersistenceException;
import servlet.FindTopKPopularRoutes;
import util.JsonReader;
import util.Utilities;


public class JenaManagerForPlace {

	private final static Logger logger = LoggerFactory.getLogger(JenaManagerForPlace.class);

	private final static String artsCategory = "Planetarium, Library, ArtsCentre, ArtGallery, Artwork, Gallery, Observatory, ArtShop, UNESCOWorldHeritage";	
	private final static String museumCategory ="Museum, HistoricMuseum";
	private final static String historyAndMonumentsCategory = "Courthouse, Artwork, GovermentBuilding, Statue, Tourist, WaterFountain, Souvenir, Souvenirs, TouristShop, Terrace, ArchaeologicalSite, Castle, Monument, HistoricBuilding, HistoricFountain, ProtectedBuilding, HistoricTower, UNESCOWorldHeritage, HistoricPointOfInterest, Tower";
	private final static String churchCategory = "PlaceOfWorship, Chapel, ChurchHall, Church, Monastery, Synagogue, Temple, Cathedral, Abbey, HistoricChurch, HistoricChapel, HistoricMonastery";
	private final static String entertainmentsCategory = "AnimalShelter, BicycleRental, ArtsCentre, Cinema, Theatre, Sauna, Shelter, Casino, ConcertHall, MusicVenue, Solarium, Spa, BeautySalon, ThemePark, Zoo, Viewpoint, Castle, LandusePark, Stadium, WaterPark, NatureReserve, Park, Garden, Beach";
	//Attenzione luoghi all'aperto
	private final static String foodCategory = "Restaurant, FastFood, Bbq, Pub, Bar, Cafe, Biergarten, IceCream, Brewery, Bakery, CoffeeShop, InternetCafe, Restaurant%3Bpub, TakeAway";
	private final static String nightLifeCategory = "Pub, Cinema, Nightclub, Stripclub, Theatre, Brothel, Brewery, Casino, byNight, Dance, Bingo";
	private final static String shopAndServiceCategory = "Marketplace, Brewery, CoffeeShop, Commercial, Florist, Hairdresser, Market, PublicMarket, Shop, Shopping, Shops, Supermarket, AnimeShop, ArtShop, Mall, Patisserie, ShoppingCenter, Souvenir";
	private final static String outdoorsAndRecreationCategory = "AnimalShelter, Biergarten, BicycleRental, Campsite, Farm, Picknick, PicnicSite, ThemePark, Zoo, Viewpoint, ArchaeologicalSite, Castle, UNESCOWorldHeritage, LandusePark, Volcano, Glacier, Peak, Grassland, Tree, Wood, CaveEntrance, Beach, Cape, Crater, Fjord, Island, Hill, Island, NaturalWaterfall, ProtectedArea, featuresSport, DogPark, WaterPark, NatureReserve, Park, Garden";
	private final static String athleticsAndSport = "Gym, Sport, SportsCentre, SportShop, Stadium, parkRide, ski, snowmobile, featuresSport, SwimmingPool, SnowPark";

	private final static String ontology_service =  "http://linkedgeodata.org/sparql";

	private final static String prefixes = "Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
			+"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+"Prefix ogc: <http://www.opengis.net/ont/geosparql#>"+"\n"
			+"Prefix geom: <http://geovocab.org/geometry#>"+"\n"
			+"Prefix lgdo: <http://linkedgeodata.org/ontology/>"+"\n"
			+"PREFIX lgdp: <http://linkedgeodata.org/property/>"+"\n"
			+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"
			+"PREFIX  g: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+"\n"
			+"PREFIX osmt: <https://wiki.openstreetmap.org/wiki/Key:>"+"\n"
			+"PREFIX lgd-addr:  <http://linkedgeodata.org/ontology/addr%3A>"+"\n";


	public static List<Venue> retriveNodes(double lat, double lon, double radius, List<String> categories) throws PersistenceException {


		Set<String> categoriesSet = createCategoriesSet(categories);

		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		List<Venue> result = new ArrayList<>();
		Map<Integer, String> mapCategory = createCategoryMap(categories);



		String category = categoriesSet.iterator().next();

		String query = "{ SELECT ?obj (SAMPLE(?l) as ?label) (SAMPLE(?lat) as ?latitudine) (SAMPLE(?long) as ?longitudine) (SAMPLE(?openHours) as ?open) (SAMPLE(?tipo) as ?category) (SAMPLE(?street) AS ?s) (SAMPLE(?number) AS ?numb) WHERE {"+"\n"
				+"?obj rdf:type ?tipo ."+"\n"
				+"FILTER regex(str(?tipo), \"http://linkedgeodata.org/ontology/"+category+"\") "+"\n"
				+"?obj a lgdo:"+category+" ."+"\n"
				+"?obj"+"\n"
				+"rdfs:label ?l ;"+"\n"
				+"g:lat ?lat ;"+"\n"
				+"g:long ?long ."+"\n"
				+"OPTIONAL { ?obj lgdo:cuisine ?cuisine } ."+"\n"
				+"OPTIONAL { ?obj lgdo:opening_hours ?openHours } ."+"\n"
				+"OPTIONAL { ?obj lgd-addr:street ?street . } "+"\n"
				+"OPTIONAL { ?obj lgd-addr:housenumber ?number . } "+"\n"
				+ "FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
				+"} "+"\n"
				+"GROUP BY ?obj"+"\n"
				+"LIMIT 0}";

		for (Iterator<String> it = categoriesSet.iterator(); it.hasNext(); ) {

			category = it.next();

			String limitValue = "100";


			for (String id : categories) {
				if(id.equals("3") || id.equals("6"))	{
					if (mapCategory.get(Integer.parseInt(id)).contains(category))	{
						limitValue = "50";
					}
				}	

			}
			


			query += "UNION "+"\n"+"{ SELECT ?obj (SAMPLE(?l) as ?label) (SAMPLE(?lat) as ?latitudine) (SAMPLE(?long) as ?longitudine) (SAMPLE(?openHours) as ?open) (SAMPLE(?tipo) as ?category) (SAMPLE(?street) AS ?s) (SAMPLE(?number) AS ?numb) WHERE {"
					+"?obj rdf:type ?tipo ."+"\n"
					+"FILTER regex(str(?tipo), \"http://linkedgeodata.org/ontology/"+category+"\") "+"\n"
					+"?obj a lgdo:"+category+" ."+"\n"
					+"?obj"+"\n"
					+"rdfs:label ?l ;"+"\n"
					+"g:lat ?lat ;"+"\n"
					+"g:long ?long ."+"\n"
					+"OPTIONAL { ?obj lgdo:cuisine ?cuisine } ."+"\n"
					+"OPTIONAL { ?obj lgdo:opening_hours ?openHours } ."+"\n"
					+"OPTIONAL { ?obj lgd-addr:street ?street . } "+"\n"
					+"OPTIONAL { ?obj lgd-addr:housenumber ?number . } "+"\n"
					+ "FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
					+"} "+"\n"
					+"GROUP BY ?obj"+"\n"
					+"LIMIT "+limitValue+"}";
		}


		String queryLGD = prefixes+"SELECT * WHERE {"+"\n"
				+query+"\n"
				+"}";


		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,queryLGD);


		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();

		String key = null;
		String secret = null;

		Properties prop = new Properties();
		InputStream input = null;

		try {

			prop.load(JenaManagerForPlace.class.getClassLoader().getResourceAsStream("config.properties"));
			// get the property value and print it out

			key = prop.getProperty("FLICKR_KEY");
			secret = prop.getProperty("FLICKR_SECRET");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

//		ResultSetFormatter.out(System.out, results);

		while (results.hasNext()) {


			Venue obj = new Venue();

			QuerySolution solution = results.next();
			String lat_s = solution.get("latitudine").toString().split("\\^")[0];
			String long_s = solution.get("longitudine").toString().split("\\^")[0];
			String label = solution.get("label").toString().split("\\@")[0];

			Flickr flickr = new Flickr(key, secret, new REST());

			label = label.replace("\\\"","");

			String[] tags=new String[]{label};

			SearchParameters searchParams=new SearchParameters();
			searchParams.setBBox(String.valueOf((Double.parseDouble(long_s)-0.1)), String.valueOf((Double.parseDouble(lat_s)-0.1)), String.valueOf((Double.parseDouble(long_s)+0.1)), String.valueOf((Double.parseDouble(lat_s)+0.1)));
			searchParams.setTags(tags);
			try {
				searchParams.setMedia("photos");
			} catch (FlickrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);

			PhotoList<Photo> list = new PhotoList<>();

			//			try {
			//				list = flickr.getPhotosInterface().search(searchParams, 10, 1);
			//			} catch (FlickrException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}


			String link = getWikiPage(label);
			obj.setExternalLink(link);
			obj.setId(Long.parseLong((solution.get("obj").toString().split("http://linkedgeodata.org/triplify/node")[1])));
			obj.setProvider("LinkedGeoData");		 
			obj.setName_fq(label);
			obj.setWhy("Close place");
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			MacroCategory mCategory = new MacroCategory();
			String categoryName = solution.get("category").toString().split("http://linkedgeodata.org/ontology/")[1];
			mCategory.setMacro_category_fq(categoryName);


			for (String id : categories) {
				if (mapCategory.get(Integer.parseInt(id)).contains(categoryName))	{
					System.out.println(Integer.parseInt(id));
					mCategory.setId(Integer.parseInt(id));
					mCategory.addMeanResidenceTime(id);
				}	

			}
			
			if(mCategory.getWeights()==null)	{
				mCategory.setId(6);
			}

			obj.setCategory_fq(categoryName);
			obj.setMacro_category(mCategory);


			if(solution.get("open")!=null)	{
				obj.setOpenHours(solution.get("open").toString());
			}
			
			if(solution.get("s")!=null && solution.get("numb")!=null)	{
				String street = solution.get("s").toString();
				String number = solution.get("numb").toString();
				String address = street.concat(number);
				obj.setAddress(address.toLowerCase());
			}

			if (!list.isEmpty()) {
				obj.setMediaUrl(list.get(0).getLargeUrl());
			}

//			CheckinPostgres.getCheckinsNumbersByVenueId(obj);

			logger.info("NAME FROM LINKED GEO DATA\t"+ label);
			result.add(obj);




		}

		return result;
	}



	private static Set<String> createCategoriesSet(List<String> categories) {

		List<String> userCategoriesLGD = new ArrayList<>();

		Map<Integer, String> categoriesMap = createCategoryMap(categories);

		for (String catValue : categoriesMap.values()) {
			List<String> catList = new ArrayList<String>(Arrays.asList(catValue.split("\\, ", -1)));
			userCategoriesLGD.addAll(catList);
		}

		return new LinkedHashSet<>(userCategoriesLGD);
	}


	public static Map<Integer, String> createCategoryMap(List<String> categories)	{

		Map<Integer, String> categoriesMap = new HashMap<>();


		if (categories.contains("1"))	{
			categoriesMap.put(1,museumCategory);
		}
		if (categories.contains("2"))	{
			categoriesMap.put(2,churchCategory);
		}
		if (categories.contains("3"))	{
			categoriesMap.put(3,historyAndMonumentsCategory);
		}
		if (categories.contains("4"))	{
			categoriesMap.put(4,artsCategory);
		}
		if (categories.contains("5"))	{
			categoriesMap.put(5,outdoorsAndRecreationCategory);
		}
		if (categories.contains("6"))	{
			categoriesMap.put(6,foodCategory);
		}
		if (categories.contains("7"))	{
			categoriesMap.put(7,entertainmentsCategory);
		}
		if (categories.contains("8"))	{
			categoriesMap.put(8,nightLifeCategory);
		}
		if (categories.contains("9"))	{
			categoriesMap.put(9,shopAndServiceCategory);
		}
		if (categories.contains("10"))	{
			categoriesMap.put(10,athleticsAndSport);
		}

		return categoriesMap;

	}



	private static String getWikiPage(String result){
		String  s=null;
		if(result!=null && !result.isEmpty()){
			result = result.substring(0).replace(" ", "_");
			try {
				s ="https://it.wikipedia.org/wiki/" +  URLEncoder.encode(result, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	public static void main(String[] args) throws PersistenceException, FileNotFoundException {

		List<String> cat = new ArrayList<>();


		
		cat.add("10");

		List<Venue> venues = JenaManagerForPlace.retriveNodes(41.8, 12.45, 0.1, cat);
		
		Filter.filterFoodVenues(venues);
		
		for (Venue venue : venues) {
			System.out.println(venue.getName_fq());
		}
		
		System.out.println(venues.size());
		

//		PrintStream out = new PrintStream(new FileOutputStream("/Users/mac/Desktop/output.txt"));
//		System.setOut(out);
		
//		JsonReader.filterClosedVenue(venues, cat);
		
		

	}
	
	


}











