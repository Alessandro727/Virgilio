package logic.router;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;

import model.MacroCategory;
import model.Venue;
import scala.reflect.internal.Trees.This;

public class JenaManagerForPlace {

	private final static Logger logger = LoggerFactory.getLogger(JenaManagerForPlace.class);

	private final static String artsCategory = "Planetarium, Library, ArtsCentre, ArtGallery, Artwork, Gallery, Observatory, ArtShop, UNESCOWorldHeritage";	
	private final static String museumCategory ="Museum, HistoricMuseum";
	private final static String historyAndMonumentsCategory = "Courthouse, Artwork, GovermentBuilding, Statue, Tourist, WaterFountain, Souvenir, Souvenirs, TouristShop, Terrace, ArchaeologicalSite, Castle, Monument, HistoricBuilding, HistoricFountain, ProtectedBuilding, HistoricTower, UNESCOWorldHeritage, HistoricPointOfInterest, Tower";
	private final static String churchCategory = "PlaceOfWorship, Chapel, ChurchHall, Church, Monastery, Synagogue, Temple, Cathedral, Abbey, HistoricChurch, HistoricChapel, HistoricMonastery";
	private final static String entertaimentsCategory = "AnimalShelter, BicycleRental, ArtsCentre, Cinema, Theatre, Sauna, Shelter, ArtGallery, Artwork, Casino, ConcertHall, MusicVenue, Solarium, Spa, BeautySalon, ThemePark, Zoo, Viewpoint, Castle, LandusePark, Stadium, WaterPark, NatureReserve, Park, Garden, Beach";
	private final static String foodCategory = "Restaurant, FastFood, Bbq, Pub, Bar, Cafe, Biergarten, IceCream, Brewery, Bakery, CoffeeShop, InternetCafe, Restaurant%3Bpub, TakeAway";
	private final static String nightLifeCategory = "Pub, Cinema, Nightclub, Stripclub, Theatre, Brothel, Brewery, Casino, byNight, Dance, Bingo";
	private final static String shopAndServiceCategory = "Marketplace, Brewery, CoffeeShop, Commercial, Florist, Hairdresser, Market, PublicMarket, Shop, Shopping, Shops, Supermarket, AlcoholShop, AnimeShop, ArtShop, Mall, Patisserie, ShoppingCenter, Souvenir";
	private final static String outdoorsAndRecreationCategory = "AnimalShelter, Biergarten, FastFood, IceCream, BicycleRental, ArtsCentre, Campsite, Farm, Picknick, PicnicSite, ThemePark, Zoo, Viewpoint, ArchaeologicalSite, Castle, UNESCOWorldHeritage, LandusePark, Volcano, Glacier, Peak, Grassland, Tree, Wood, CaveEntrance, Beach, Cape, Crater, Fjord, Island, Hill, Island, NaturalWaterfall, ProtectedArea, featuresSport, DogPark, WaterPark, NatureReserve, Park, Garden";
	private final static String athleticsAndSport = "Gym, Sport, SportsCentre, SwimmingPool, SportShop, Stadium";

	private final static String ontology_service =  "http://linkedgeodata.org/sparql";

	private final static String prefixes = "Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
			+"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+"Prefix ogc: <http://www.opengis.net/ont/geosparql#>"+"\n"
			+"Prefix geom: <http://geovocab.org/geometry#>"+"\n"
			+"Prefix lgdo: <http://linkedgeodata.org/ontology/>"+"\n"
			+"PREFIX lgdp: <http://linkedgeodata.org/property/>"+"\n"
			+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"
			+"PREFIX  g: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+"\n"
			+"PREFIX osmt: <https://wiki.openstreetmap.org/wiki/Key:>"+"\n";


	public static List<Venue> retrivePlacesNodes(double lat, double lon, double radius, String[] categories) {


		Set<String> categoriesSet = createCaretoriesSet(categories);

		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		List<Venue> result = new ArrayList<>();


		String category = categoriesSet.iterator().next();

		String query = "{ SELECT ?obj (SAMPLE(?l) as ?label) (SAMPLE(?lat) as ?latitudine) (SAMPLE(?long) as ?longitudine) (SAMPLE(?openHours) as ?open) (SAMPLE(?tipo) as ?category) WHERE {"+"\n"
				+"?obj rdf:type ?tipo ."+"\n"
				+"FILTER regex(str(?tipo), \"http://linkedgeodata.org/ontology/"+category+"\") "+"\n"
				+"?obj a lgdo:"+category+" ."+"\n"
				+"?obj"+"\n"
				+"rdfs:label ?l ;"+"\n"
				+"g:lat ?lat ;"+"\n"
				+"g:long ?long ."+"\n"
				+"OPTIONAL { ?obj lgdo:cuisine ?cuisine } ."+"\n"
				+"OPTIONAL { ?obj lgdo:opening_hours ?openHours } ."+"\n"
				+ "FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
				+"} "+"\n"
				+"GROUP BY ?obj"+"\n"
				+"LIMIT 0}";

		for (Iterator<String> it = categoriesSet.iterator(); it.hasNext(); ) {

			category = it.next();

			query += "UNION "+"\n"+"{ SELECT ?obj (SAMPLE(?l) as ?label) (SAMPLE(?lat) as ?latitudine) (SAMPLE(?long) as ?longitudine) (SAMPLE(?openHours) as ?open) (SAMPLE(?tipo) as ?category) WHERE {"
					+"?obj rdf:type ?tipo ."+"\n"
					+"FILTER regex(str(?tipo), \"http://linkedgeodata.org/ontology/"+category+"\") "+"\n"
					+"?obj a lgdo:"+category+" ."+"\n"
					+"?obj"+"\n"
					+"rdfs:label ?l ;"+"\n"
					+"g:lat ?lat ;"+"\n"
					+"g:long ?long ."+"\n"
					+"OPTIONAL { ?obj lgdo:cuisine ?cuisine } ."+"\n"
					+"OPTIONAL { ?obj lgdo:opening_hours ?openHours } ."+"\n"
					+ "FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
					+"} "+"\n"
					+"GROUP BY ?obj"+"\n"
					+"LIMIT 10}";
		}

		System.out.println(query);

		String queryLGD = prefixes+"SELECT * WHERE {"+"\n"
				+query+"\n"
				+"}";


		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,queryLGD);


		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();

		String key = null;
		String secret = null;

		InputStream inputStream = 
				JenaManagerForPlace.class.getClassLoader().getResourceAsStream("config.txt");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream ));

		String sCurrentLine;

		try {
			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				if (sCurrentLine.contains("FLICKR_KEY"))	{
					key =  sCurrentLine.split("FLICKR_KEY=")[1];
				}
				if (sCurrentLine.contains("FLICKR_SECRET"))	{
					secret =  sCurrentLine.split("FLICKR_SECRET=")[1];
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

			try {
				list = flickr.getPhotosInterface().search(searchParams, 10, 1);
			} catch (FlickrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


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
			
			List<String> userCategories = 
					new ArrayList<String>(Arrays.asList(categories));
			
			for (String string : userCategories) {
				
			}
			
			
			obj.setMacro_category(mCategory);

			System.out.println(label);

			if(solution.get("open")!=null)	{
				obj.setOpenHours(solution.get("open").toString());
			}

			if (!list.isEmpty()) {
				obj.setMediaUrl(list.get(0).getLargeUrl());
				System.out.println(list.get(0).getLargeUrl());
			}

			logger.info("NAME FROM LINKED GEO DATA\t"+ label);
			result.add(obj);


		}

		return result;
	}



	private Set<String> createCaretoriesSet(String[] categories) {
		
		List<String> userCategoriesLGD = new ArrayList<>();

		Map<Integer, String> categoriesMap = this.createCategoryMap(String[] categories);

		for (String catValue : categoriesMap.values()) {
			List<String> catList = new ArrayList<String>(Arrays.asList(catValue.split("\\, ", -1)));
			userCategoriesLGD.addAll(catList);
		}

		return new LinkedHashSet<>(userCategoriesLGD);
	}
	
	
	public Map<Integer, String> createCategoryMap(String[] categories)	{
		
		Map<Integer, String> categoriesMap = new HashMap<>();
		List<String> userCategoriesLGD = new ArrayList<>();

		List<String> userCategories = 
				new ArrayList<String>(Arrays.asList(categories));

		if (userCategories.contains("1"))	{
			categoriesMap.put(1,artsCategory);
		}
		if (userCategories.contains("2"))	{
			categoriesMap.put(2,entertaimentsCategory);
		}
		if (userCategories.contains("3"))	{
			categoriesMap.put(3,museumCategory);
		}
		if (userCategories.contains("5"))	{
			categoriesMap.put(5,foodCategory);
		}
		if (userCategories.contains("6"))	{
			categoriesMap.put(6,nightLifeCategory);
		}
		if (userCategories.contains("7"))	{
			categoriesMap.put(7,outdoorsAndRecreationCategory);
		}
		if (userCategories.contains("8"))	{
			categoriesMap.put(8,historyAndMonumentsCategory);
		}
		if (userCategories.contains("9"))	{
			categoriesMap.put(9,churchCategory);
		}
		if (userCategories.contains("10"))	{
			categoriesMap.put(10,shopAndServiceCategory);
		}
		if (userCategories.contains("11"))	{
			categoriesMap.put(11,athleticsAndSport);
		}
		
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


}
