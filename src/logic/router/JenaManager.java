package logic.router;

import model.Book;
import model.Couple;
import model.MacroCategory;
import model.Singer;
import model.Venue;
import scala.annotation.StaticAnnotation;
import socialAndServices.Foursquare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.xerces.xinclude.MultipleScopeNamespaceSupport;
import org.omg.PortableInterceptor.NON_EXISTENT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.places.Place;
import com.flickr4java.flickr.places.PlacesList;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.restfb.types.ResumableUploadTransferResponse;

import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.BuyLink;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Chart;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.entities.CompactVenue;



public class JenaManager {

	private final static Logger logger = LoggerFactory.getLogger(JenaManager.class);



	@Inject
	public JenaManager() {

	}

	public static void main(String[] args) {
		//				 String cm = "dbpedia:Girl_with_a_Pearl_Earring";
		//				 String cx = "dbpedia:Johannes_Vermeer";
		//		String cm = "<http://dbpedia.org/resource/Colosseum>";
		//		String cx = "<http://dbpedia.org/resource/Vespasian>";
		//		List<String> objs = JenaManager.conceptQuery(cm, cx);
		//		System.out.println(objs.size());
		//		for(String s: objs)
		//			System.out.println(s);

		String[] category = {"3"};



		//				List<Venue> result = JenaManager.retrievePlaceNodeFromLinkedGeoData(41.45, 12.71, 0.5, category);

		//		Map<Long, Book> resultBook = JenaManager.retrieveBookNodeFromWikiData(41.45, 12.71, 1.0);

		JenaManager.retrieveSongNodeFromWikiDataAndLastFM(41.45, 12.71, 0.5);
		//				Venue venue = new Venue();
		//				venue.setLatitude("41.9151");
		//				venue.setLongitude("12.4865");
		//				venue.setName_fq("Museo Pietro Canonica");
		//		
		//				CompactVenue compactVenue = new CompactVenue();
		//				try {
		//					compactVenue = Foursquare.searchSingleVenueMatch(venue);
		//				} catch (FoursquareApiException e) {
		//					// TODO Auto-generated catch block
		//					System.out.println("ECCEZIONE");
		//					e.printStackTrace();
		//				}
		//				
		//				System.out.println(compactVenue.getStats().toString());



		//		CompactVenue[] resultVenue = new CompactVenue[20];
		//		try {
		//			resultVenue = Foursquare.searchVenues("41.894464, 12.490668");
		//		} catch (FoursquareApiException e) {
		//			// TODO Auto-generated catch block
		//			System.out.println("NO");
		//			e.printStackTrace();
		//		}
		//		
		//		for (int i=0; i<resultVenue.length; i++)	{
		//			
		//			System.out.println(resultVenue[i].getName());
		//		
		//		}



	}



	public static List<Venue> retrievePlaceNodeFromLinkedGeoData(double lat, double lon, double radius, String[] categories) {



		final String artsCategory = "Planetarium, Library, ArtsCentre, ArtGallery, Artwork, Gallery, Observatory, ArtShop, UNESCOWorldHeritage";	
		List<String> arrayArts = new ArrayList<String>(Arrays.asList(artsCategory.split("\\, ", -1)));

		String museumCategory ="Museum, HistoricMuseum";
		List<String> arrayMuseum = new ArrayList<String>(Arrays.asList(museumCategory.split("\\, ", -1)));

		String historyAndMonumentsCategory = "Courthouse, Artwork, GovermentBuilding, Statue, Tourist, WaterFountain, Souvenir, Souvenirs, TouristShop, Terrace, ArchaeologicalSite, Castle, Monument, HistoricBuilding, HistoricFountain, ProtectedBuilding, HistoricTower, UNESCOWorldHeritage, HistoricPointOfInterest, Tower";
		List<String> arrayHistoryAndMonument = new ArrayList<String>(Arrays.asList(historyAndMonumentsCategory.split("\\, ", -1)));

		String churchCategory = "PlaceOfWorship, Chapel, ChurchHall, Church, Monastery, Synagogue, Temple, Cathedral, Abbey, HistoricChurch, HistoricChapel, HistoricMonastery";
		List<String> arrayChurch = new ArrayList<String>(Arrays.asList(churchCategory.split("\\, ", -1)));

		String entertaimentsCategory = "AnimalShelter, BicycleRental, ArtsCentre, Cinema, Theatre, Sauna, Shelter, ArtGallery, Artwork, Casino, ConcertHall, MusicVenue, Solarium, Spa, BeautySalon, ThemePark, Zoo, Viewpoint, Castle, LandusePark, Stadium, WaterPark, NatureReserve, Park, Garden, Beach";
		List<String> arrayEntertaiments = new ArrayList<String>(Arrays.asList(entertaimentsCategory.split("\\, ", -1)));

		String foodCategory = "Restaurant, FastFood, Bbq, Pub, Bar, Cafe, Biergarten, IceCream, Brewery, Bakery, CoffeeShop, InternetCafe, Restaurant%3Bpub, TakeAway";
		List<String> arrayFood = new ArrayList<String>(Arrays.asList(foodCategory.split("\\, ", -1)));

		String nightLifeCategory = "Pub, Cinema, Nightclub, Stripclub, Theatre, Brothel, Brewery, Casino, byNight, Dance, Bingo";
		List<String> arrayNightLife = new ArrayList<String>(Arrays.asList(nightLifeCategory.split("\\, ", -1)));

		String shopAndServiceCategory = "Marketplace, Brewery, CoffeeShop, Commercial, Florist, Hairdresser, Market, PublicMarket, Shop, Shopping, Shops, Supermarket, AlcoholShop, AnimeShop, ArtShop, Mall, Patisserie, ShoppingCenter, Souvenir";
		List<String> arrayShopAndService = new ArrayList<String>(Arrays.asList(shopAndServiceCategory.split("\\, ", -1)));

		String outdoorsAndRecreationCategory = "AnimalShelter, Biergarten, FastFood, IceCream, BicycleRental, ArtsCentre, Campsite, Farm, Picknick, PicnicSite, ThemePark, Zoo, Viewpoint, ArchaeologicalSite, Castle, UNESCOWorldHeritage, LandusePark, Volcano, Glacier, Peak, Grassland, Tree, Wood, CaveEntrance, Beach, Cape, Crater, Fjord, Island, Hill, Island, NaturalWaterfall, ProtectedArea, featuresSport, DogPark, WaterPark, NatureReserve, Park, Garden";
		List<String> arrayOutDoorsAndRecreation = new ArrayList<String>(Arrays.asList(outdoorsAndRecreationCategory.split("\\, ", -1)));

		String athleticsAndSport = "Gym, Sport, SportsCentre, SwimmingPool, SportShop, Stadium";
		List<String> arrayAthleticsAndSport = new ArrayList<String>(Arrays.asList(athleticsAndSport.split("\\, ", -1)));

		List<String> userCategoriesLGD = new ArrayList<>();

		List<String> userCategories = 
				new ArrayList<String>(Arrays.asList(categories));

		if (userCategories.contains("1"))	{
			userCategoriesLGD.addAll(arrayArts);
		}

		if (userCategories.contains("2"))	{
			userCategoriesLGD.addAll(arrayEntertaiments);
		}

		if (userCategories.contains("3"))	{
			userCategoriesLGD.addAll(arrayMuseum);
		}

		if (userCategories.contains("5"))	{
			userCategoriesLGD.addAll(arrayFood);
		}

		if (userCategories.contains("6"))	{
			userCategoriesLGD.addAll(arrayNightLife);
		}

		if (userCategories.contains("7"))	{
			userCategoriesLGD.addAll(arrayOutDoorsAndRecreation);
		}

		if (userCategories.contains("8"))	{
			userCategoriesLGD.addAll(arrayHistoryAndMonument);
		}

		if (userCategories.contains("9"))	{
			userCategoriesLGD.addAll(arrayChurch);
		}

		if (userCategories.contains("10"))	{
			userCategoriesLGD.addAll(arrayShopAndService);
		}

		if (userCategories.contains("11"))	{
			userCategoriesLGD.addAll(arrayAthleticsAndSport);
		}

		Set<String> categoriesSet = new LinkedHashSet<>(userCategoriesLGD);

		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		List<Venue> result = new ArrayList<>();


		String ontology_service =  "http://linkedgeodata.org/sparql";

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

		String queryLGD = "Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
				+"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+"Prefix ogc: <http://www.opengis.net/ont/geosparql#>"+"\n"
				+"Prefix geom: <http://geovocab.org/geometry#>"+"\n"
				+"Prefix lgdo: <http://linkedgeodata.org/ontology/>"+"\n"
				+"PREFIX lgdp: <http://linkedgeodata.org/property/>"+"\n"
				+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"
				+"PREFIX  g: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+"\n"
				+"PREFIX osmt: <https://wiki.openstreetmap.org/wiki/Key:>"+"\n"
				+"SELECT * WHERE {"+"\n"
				+query+"\n"
				+"}";


		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,queryLGD);




		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();


		//		ResultSetFormatter.out(System.out, results);

		while (results.hasNext()) {



			Venue obj = new Venue();

			QuerySolution solution = results.next();
			String lat_s = solution.get("latitudine").toString().split("\\^")[0];
			String long_s = solution.get("longitudine").toString().split("\\^")[0];
			String label = solution.get("label").toString().split("\\@")[0];

			Flickr flickr = new Flickr("f9ab674ce9f9deb95de6cef1dae510a1", "3fa8105f2df6bbf2", new REST());

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
			mCategory.setMacro_category_fq(solution.get("category").toString().split("http://linkedgeodata.org/ontology/")[1]);
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






		System.out.println(result.size());



		return result;
	}


	public static Map<Long, Book> retrieveBookNodeFromWikiData(double lat, double lon, double radius)	{


		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		//		List<Book> result = new ArrayList<>();
		Map<Long, Book> bookResult = new HashMap<>();


		String ontology_serviceBook =  "https://query.wikidata.org/sparql";

		String queryBook = "PREFIX wd: <http://www.wikidata.org/entity/>"+"\n"
				+"PREFIX wdt: <http://www.wikidata.org/prop/direct/>"+"\n"
				+"PREFIX wikibase: <http://wikiba.se/ontology#>"+"\n"
				+"PREFIX p: <http://www.wikidata.org/prop/>"+"\n"
				+"PREFIX ps: <http://www.wikidata.org/prop/statement/>"+"\n"
				+"PREFIX pq: <http://www.wikidata.org/prop/qualifier/>"+"\n"
				+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
				+"PREFIX bd: <http://www.bigdata.com/rdf#>"+"\n"
				+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"

				+"SELECT DISTINCT ?item ?itemLabel  ?auteurLabel ?placeLabel (SAMPLE(?coord) AS ?_coord) (SAMPLE(?linkcount) AS ?link_count) (SAMPLE(?connection) AS ?conn) ?genreLabel (SAMPLE(?image) AS ?img) WHERE {"+"\n"
				+"{?item wdt:P31 wd:Q571 .} UNION {?item wdt:P31 wd:Q7725634}."+"\n"
				+"?item wdt:P136 ?genre ."+"\n"
				+"OPTIONAL {  ?item wdt:P18 ?image . } "+"\n"
				+ "?item wdt:P50 ?auteur ."+"\n"
				+"?item wdt:P840 ?place ."+"\n"
				+"?item wikibase:sitelinks ?linkcount ."+"\n"
				+"SERVICE wikibase:label { bd:serviceParam wikibase:language \"it,en,fr,ar,be,bg,bn,ca,cs,da,de,el,es,et,fa,fi,he,hi,hu,hy,id,ja,jv,ko,nb,nl,eo,pa,pl,pt,ro,ru,sh,sk,sr,sv,sw,te,th,tr,uk,yue,vec,vi,zh\" . }"+"\n"
				+"MINUS { ?item wdt:P840 wd:Q933 . }"+"\n"
				+"SERVICE wikibase:box {"+"\n"
				+"?place wdt:P625 ?coord ."+"\n"
				+"bd:serviceParam wikibase:cornerWest \"Point("+lon1+" "+lat1+")\"^^geo:wktLiteral ."+"\n"
				+"bd:serviceParam wikibase:cornerEast \"Point("+lon2+" "+lat2+")\"^^geo:wktLiteral ."+"\n"
				+"}"+"\n"

	  			+"OPTIONAL{ ?item wdt:P953 ?connection . }"+"\n"


				+"}"+"\n"
				+"GROUP BY ?item ?itemLabel ?auteurLabel ?placeLabel ?genreLabel"+"\n"
				+"ORDER BY DESC(?link_count)";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_serviceBook,
				queryBook);



		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();

		//		ResultSetFormatter.out(System.out, results);

		while (results.hasNext()) {



			Book obj = new Book();

			QuerySolution solution = results.next();

			String point = solution.get("_coord").toString().split("\\^")[0].split("\\(")[1].split("\\)")[0];
			String lat_s = point.split(" ")[1];
			String long_s = point.split(" ")[0];
			long id = Long.parseLong((solution.get("item").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String autor = solution.get("auteurLabel").toString().split("\\@")[0];
			String label = solution.get("itemLabel").toString().split("\\@")[0];
			String genre = solution.get("genreLabel").toString().split("\\@")[0];

			Flickr flickr = new Flickr("f9ab674ce9f9deb95de6cef1dae510a1", "3fa8105f2df6bbf2", new REST());

			label = label.replace("\\\"","");
			autor = autor.replace("\\\"","");

			//			System.out.println(label);


			obj.setName(label);
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			obj.setId(id);
			obj.setCreator(autor);
			obj.getGenres().add(genre);

			if (solution.get("conn")!=null)	{
				String link = solution.get("conn").toString();
				obj.setExternalLink(link);

			}

			if (solution.get("img")!=null)	{
				String img = solution.get("img").toString();
				obj.setImage(img);

			}

			String[] tags=new String[]{label,"book",autor};




			SearchParameters searchParams=new SearchParameters();
			searchParams.setTags(tags);

			searchParams.setTagMode("all");

			try {
				searchParams.setMedia("photos");
			} catch (FlickrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			searchParams.setSort(SearchParameters.RELEVANCE);

			PhotoList<Photo> list = new PhotoList<>();

			try {
				list = flickr.getPhotosInterface().search(searchParams, 10, 1);
			} catch (FlickrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (obj.getImage()==null)
				if (!list.isEmpty()) {
					obj.setImage(list.get(0).getLargeUrl());
				}

			logger.info("NAME FROM WIKI DATA\t"+ label);

			if (!bookResult.containsKey(id))
				bookResult.put(id, obj);
			else {
				bookResult.get(id).getGenres().add(genre);

			}

		}

		return bookResult;

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


	public static Map<Long, Singer> retrieveSongNodeFromWikiDataAndLastFM(double lat, double lon, double radius)	{


		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		Map<Long, Singer> singerResult = new HashMap<>();


		String ontology_serviceTrack =  "https://query.wikidata.org/sparql";

		String queryTrack = "PREFIX wd: <http://www.wikidata.org/entity/>"+"\n"
				+"PREFIX wdt: <http://www.wikidata.org/prop/direct/>"+"\n"
				+"PREFIX wikibase: <http://wikiba.se/ontology#>"+"\n"
				+"PREFIX p: <http://www.wikidata.org/prop/>"+"\n"
				+"PREFIX ps: <http://www.wikidata.org/prop/statement/>"+"\n"
				+"PREFIX pq: <http://www.wikidata.org/prop/qualifier/>"+"\n"
				+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
				+"PREFIX bd: <http://www.bigdata.com/rdf#>"+"\n"
				+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"
				+"PREFIX psv: <http://www.wikidata.org/prop/statement/value/>"+"\n"
				+"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+"\n"

					+"SELECT ?artista ?artistaLabel ?birthPlaceLabel ?lat ?long ?genereLabel WHERE {"+"\n"
					+"SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }"+"\n"
					+"?artista wdt:P106 wd:Q177220."+"\n"
					+"?artista wdt:P19 ?birthPlace."+"\n"
					+"?birthPlace p:P625 ?coordinate."+"\n"
					+"?coordinate psv:P625 ?coordinate_node."+"\n"
					+"?coordinate_node wikibase:geoLongitude ?long."+"\n"
					+"?coordinate_node wikibase:geoLatitude ?lat."+"\n"
					+"?artista wdt:P136 ?genere."+"\n"
					+"FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
					+"}"+"\n";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_serviceTrack,
				queryTrack);



		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();

//		ResultSetFormatter.out(System.out, results);

		while (results.hasNext()) {

			Singer obj = new Singer();

			QuerySolution solution = results.next();


			String lat_s = solution.get("lat").toString();
			String long_s = solution.get("long").toString();
			long id = Long.parseLong((solution.get("artista").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String label = solution.get("artistaLabel").toString().split("\\@")[0];
			String genre = solution.get("genereLabel").toString().split("\\@")[0];

			Flickr flickr = new Flickr("f9ab674ce9f9deb95de6cef1dae510a1", "3fa8105f2df6bbf2", new REST());

			label = label.replace("\\\"","");

			obj.setName(label);
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			obj.setId(id);

			obj.getGenres().add(genre);
			logger.info("NAME FROM LAST.FM AND WIKI DATA\t"+ label);
			
			String key = null; //this is the key used in the Last.fm API 
			
			FileReader fReader = null;
			try {
				fReader = new FileReader("config.txt");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			BufferedReader bufferedReader = new BufferedReader(fReader);
			
			String sCurrentLine;

			try {
				while ((sCurrentLine = bufferedReader.readLine()) != null) {
					if (sCurrentLine.contains("LAST.FM_KEY"))	{
						key =  sCurrentLine.split("LAST.FM_KEY=")[1];
					}
						
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			if (!singerResult.containsKey(id))	{
				 

				Collection<Track> topTracks = Artist.getTopTracks(label, key);
				System.out.println("Top Tracks for "+label+":");


				for (Track track : topTracks) {
					
					System.out.println(track.getName());

					obj.getSong().add(track.getName()); 
					obj.setPlayCount(track.getPlaycount()); 
					obj.setExternalLink(track.getUrl()); 
					obj.setImage(track.getImageURL(ImageSize.LARGE));
				}
				
				singerResult.put(id, obj);
			}
				
			else {
				singerResult.get(id).getGenres().add(genre);

			}

		}
		
		System.out.println(singerResult.size());
		
		return singerResult;

	}
	
	
	public static Map<Long, Singer> retrieveMovieNodeFromWikiDataAndIMDb(double lat, double lon, double radius)	{


		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		Map<Long, Singer> singerResult = new HashMap<>();


		String ontology_serviceTrack =  "https://query.wikidata.org/sparql";

		String queryTrack = "PREFIX wd: <http://www.wikidata.org/entity/>"+"\n"
				+"PREFIX wdt: <http://www.wikidata.org/prop/direct/>"+"\n"
				+"PREFIX wikibase: <http://wikiba.se/ontology#>"+"\n"
				+"PREFIX p: <http://www.wikidata.org/prop/>"+"\n"
				+"PREFIX ps: <http://www.wikidata.org/prop/statement/>"+"\n"
				+"PREFIX pq: <http://www.wikidata.org/prop/qualifier/>"+"\n"
				+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
				+"PREFIX bd: <http://www.bigdata.com/rdf#>"+"\n"
				+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"
				+"PREFIX psv: <http://www.wikidata.org/prop/statement/value/>"+"\n"
				+"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+"\n"

					+"SELECT ?artista ?artistaLabel ?birthPlaceLabel ?lat ?long ?genereLabel WHERE {"+"\n"
					+"SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }"+"\n"
					+"?artista wdt:P106 wd:Q177220."+"\n"
					+"?artista wdt:P19 ?birthPlace."+"\n"
					+"?birthPlace p:P625 ?coordinate."+"\n"
					+"?coordinate psv:P625 ?coordinate_node."+"\n"
					+"?coordinate_node wikibase:geoLongitude ?long."+"\n"
					+"?coordinate_node wikibase:geoLatitude ?lat."+"\n"
					+"?artista wdt:P136 ?genere."+"\n"
					+"FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
					+"}"+"\n";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_serviceTrack,
				queryTrack);



		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();

//		ResultSetFormatter.out(System.out, results);
		
		String key = null;
		
		
		while (results.hasNext()) {



			Singer obj = new Singer();

			QuerySolution solution = results.next();


			String lat_s = solution.get("lat").toString();
			String long_s = solution.get("long").toString();
			long id = Long.parseLong((solution.get("artista").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String label = solution.get("artistaLabel").toString().split("\\@")[0];
			String genre = solution.get("genereLabel").toString().split("\\@")[0];

			Flickr flickr = new Flickr("f9ab674ce9f9deb95de6cef1dae510a1", "3fa8105f2df6bbf2", new REST());

			label = label.replace("\\\"","");

			obj.setName(label);
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			obj.setId(id);

			obj.getGenres().add(genre);
			logger.info("NAME FROM LAST.FM AND WIKI DATA\t"+ label);
			

			if (!singerResult.containsKey(id))	{
				 //this is the key used in the Last.fm API examples

				Collection<Track> topTracks = Artist.getTopTracks(label, key);
				System.out.println("Top Tracks for "+label+":");


				for (Track track : topTracks) {

					obj.getSong().add(track.getName()); 
					obj.setPlayCount(track.getPlaycount()); 
					obj.setExternalLink(track.getUrl()); 
					obj.setImage(track.getImageURL(ImageSize.LARGE));
				}
				
				singerResult.put(id, obj);
			}
				
			else {
				singerResult.get(id).getGenres().add(genre);

			}

		}
		
		System.out.println(singerResult.size());
		
		return singerResult;

	}
	



}
