package logic.router;

import model.Couple;
import model.Venue;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
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
import com.google.inject.name.Named;


public class JenaManager {

	private final static Logger logger = LoggerFactory.getLogger(JenaManager.class);
	private final String dbpediaEndpoint;

	private final static String PREFIX = "PREFIX dcterms:<http://purl.org/dc/terms/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX dbp-prop: <http://dbpedia.org/property/> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#>"
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> ";


	private final static String PREFIX_LOD = "PREFIX dbp-prop: <http://dbpedia.org/property/> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
			+ "PREFIX lgdr:<http://linkedgeodata.org/triplify/>" 
			+ "PREFIX lgdo:<http://linkedgeodata.org/ontology/> "
			+ "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#>"
			+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>";

	@Inject
	public JenaManager(@Named("dbpedia_endpoint") String dbpediaEndpoint) {
		this.dbpediaEndpoint = dbpediaEndpoint;
	}


	public static List<Venue> constructQuery(){
		String ontology_service = "http://dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX logdo: <http://linkedgeodata.org/ontology/> "
				+ "PREFIX dbo: <http://dbpedia.org/ontology/> "
				+ "PREFIX yago: <http://dbpedia.org/class/yago/> "
				+ "PREFIX my: <http://exampe.org/ontology/>"
				+ "construct { "
				+ "?entity a ?mytype ; ?myprop1 ?value1 . "
				+ "?entity ?myprop2 ?value2 . "
				+ "?entity ?myprop3 ?value3  "
				+ "} where { "
				+ "values (?dbtype ?mytype) { "
				+ "(dbo:Museum my:Museo) "
				+ "(logdo:Museum my:Museo) "
				+ "(yago:Museum103800563 my:Museo) "
				+ "} "
				+ "values (?dbprop1 ?myprop1) { "
				+ "(rdfs:label my:label) "
				+ "} "
				+ "values (?dbprop2 ?myprop2) { "
				+ "(geo:lat my:lat) "
				+ "} "
				+ "values (?dbprop3 ?myprop3) { "
				+ "(geo:long my:long) "
				+ "} "
				+ "?entity a ?dbtype ; ?dbprop1 ?value1 ; ?dbprop2 ?value2 ; ?dbprop3 ?value3 "
				+ "FILTER ( ?value2 > 41.00 && ?value2 < 42.00 && ?value3 > 12.00 && ?value3 < 13.00)}";


		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		//		ResultSet results = queryExecution.execSelect();

		Model results = queryExecution.execConstruct();
		results.write(System.out, "TURTLE");


		String query = "PREFIX ns2: <http://exampe.org/ontology/> "
				+ "SELECT ?m ?museo ?lat ?lon where {"
				+ "?m a ns2:Museo ."
				+ "?m ns2:lat ?lat ."
				+ "?m ns2:long ?lon ."
				+ "?m ns2:label ?museo ."
				+ "}";

		queryExecution = QueryExecutionFactory.create(query, results);
		ResultSet results2 = queryExecution.execSelect();
		List<Venue> retrievedObjects = new ArrayList<Venue>();
		String mediaUrl = "";

		while (results2.hasNext()) {
			QuerySolution solution = results2.next();
			Venue obj = new Venue();
			mediaUrl = solution.get("m").toString();

			double lat = getNumber(solution.get("lat").toString());
			double lon = getNumber(solution.get("lon").toString());

			obj.setMediaUrl(mediaUrl);
			obj.setLatitude(String.valueOf(lat));
			obj.setLongitude(String.valueOf(lon));
			retrievedObjects.add(obj);
		}


		return retrievedObjects;

	}

	private static double getNumber(String s){
		String temp = s.split("\\^")[0];
		return Double.parseDouble(temp);

	}

	// TODO: you could substitute it with europeana object
	public List<Venue> queryEuropeana(String term) {

		System.out.println("query europeana------"+term+"------");

		String ontology_service = "http://europeana.ontotext.com/sparql";
		String endpoint = "otee:Endpoints";
		// String endpointsSparql =
		// "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX edm: <http://www.europeana.eu/schemas/edm/> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/>  SELECT distinct ?proxy ?title ?subject ?mediaURL WHERE {     FILTER (contains(?subject, '"
		// + term
		// + "') || contains(?title, '"
		// + term
		// +
		// "')) ?resource ore:proxyIn ?proxy ; dc:title ?title ; dc:subject ?sunject; dc:creator ?creator ; dc:source ?source . ?proxy edm:isShownBy ?mediaURL .  } limit 50";
		term = term.replace("'", "");
		StringBuilder queryString = new StringBuilder();
		queryString.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append("PREFIX edm: <http://www.europeana.eu/schemas/edm/> ");
		queryString.append("PREFIX ore: <http://www.openarchives.org/ore/terms/> ");
		queryString.append("PREFIX dc: <http://purl.org/dc/elements/1.1/> ");
		queryString.append("SELECT distinct ?proxy ?creator ?mediaURL ?provider ?title ?source ?cho"); 
		// at
		// the
		// moment
		// we
		// don't
		// need
		// to
		// return
		// the
		// subject
		queryString.append(" WHERE { ?s dc:creator ?creator; ");
		queryString.append(" ore:proxyIn ?proxy; ");
		queryString.append(" dc:subject ?subject; ");
		queryString.append(" dc:title ?title;");
		queryString.append(" dc:title ?source;");
		queryString.append(" dc:type ?type.");
		queryString.append(" ?proxy edm:isShownBy ?mediaURL. ");
		queryString.append(" ?proxy edm:dataProvider ?provider. ");
		queryString.append(" ?proxy edm:aggregatedCHO ?cho. ");
		queryString.append(" {    ?s dc:title '" + term + "' .} UNION ");
		queryString.append(" {     ?s dc:subject '" + term + "' .} } LIMIT 100");

		// QueryExecution queryExecution =
		// QueryExecutionFactory.sparqlService(ontology_service,
		// String.format(endpointsSparql, endpoint));
		// QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
		// String.format(queryString.toString(), endpoint));
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				queryString.toString());

		ResultSet results = queryExecution.execSelect();

		List<Venue> retrievedObjects = new ArrayList<Venue>();
		String prevUri = "";
		Venue retrievedObject = new Venue();

		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String title = solution.get("title").toString();
			String uri = solution.get("proxy").toString();
			String mediaUrl = solution.get("mediaURL").toString();
			String creator = solution.get("creator").toString();
			String provider = solution.get("provider").toString();
			String source = solution.get("source").toString();
			String externalLink = "http://europeana.ontotext.com/resource?uri=" + solution.get("cho").toString();

			if (!prevUri.equals(uri)) { // different object, create it and add
				// it to the collection
				retrievedObject = new Venue();
				retrievedObject.setName_fq(title);
				retrievedObject.setLink_fq(uri);
				retrievedObject.setMediaUrl(mediaUrl);
				retrievedObject.setCreator(creator);
				retrievedObject.setProvider(provider);
				retrievedObject.setWhy(term);
				retrievedObject.setSource(source);
				retrievedObject.setExternalLink(externalLink);
				retrievedObjects.add(retrievedObject);
			} else { // same object as before, update the creator values
				retrievedObject.setCreator(retrievedObject.getCreator() + " ; " + creator);
			}
			prevUri = uri;
		}

		return retrievedObjects;
	}

	/**
	 * QUERIES DBPEDIA AND RETURNS ALL THE PLACES THAT ARE GEOLOCALLY CLOSER TO
	 * THE GIVEN URI AND THAT SHARE A CATEGORY WITH IT
	 * */
	public List<String> findCloserPlacesFromDbpedia(String term) {


		System.out.println("find closer place------"+term+"------");



		String ontology_service = this.dbpediaEndpoint;
		String endpoint = "otee:Endpoints";
		term = "<" + term + ">";
		StringBuilder queryString = new StringBuilder();
		queryString.append(" PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append(" PREFIX dbp-prop: <http://dbpedia.org/property/> ");
		queryString.append(" PREFIX dbpedia: <http://dbpedia.org/resource/> ");
		queryString.append(" PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		queryString.append(" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		queryString.append(" PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		queryString.append(" PREFIX dcterms:<http://purl.org/dc/terms/> ");

		queryString.append("  SELECT distinct ?concept");
		queryString.append(" WHERE { " + term + "  geo:lat ?uriLat. ");
		queryString.append(term + "  geo:long ?uriLong.");
		queryString.append(term + " dcterms:subject ?sub.");
		queryString.append("?concept geo:lat ?lat. ");
		queryString.append("?concept geo:long ?long. ");
		queryString.append("?concept dcterms:subject ?sub. ");
		queryString.append("FILTER(?lat - ?uriLat <= 0.05 && ?uriLat - ?lat <= 0.05 &&");
		queryString.append("?long - ?uriLong <= 0.05 && ?uriLong - ?long <= 0.05 &&");
		queryString.append("?concept!=" + term + ").} LIMIT 10");

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service, queryString.toString());
		ResultSet results = queryExecution.execSelect();

		List<String> retrievedConcepts = new ArrayList<String>();

		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String concept = solution.get("concept").toString();
			retrievedConcepts.add(concept);
		}

		return retrievedConcepts;
	}

	/**
	 * QUERIES DBPEDIA AND RETURNS THE LAT AND LANG OF THE GIVEN URI. THE URI
	 * SHOULD BE ABOUT A PLACE
	 * */
	public Couple<String, String> retrieveLatLangFromDbpedia(String term) {
		String ontology_service = this.dbpediaEndpoint;
		// String ontology_service = "http://live.dbpedia.org/sparql";

		System.out.println("retrieveLatLangFromDbpedia------"+term+"------");


		String endpoint = "otee:Endpoints";
		term = "<" + term + ">";
		StringBuilder queryString = new StringBuilder();
		queryString.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append(" PREFIX dbp-prop: <http://dbpedia.org/property/> ");
		queryString.append(" PREFIX dbpedia: <http://dbpedia.org/resource/> ");
		queryString.append(" PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		queryString.append(" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		queryString.append(" PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		queryString.append(" PREFIX dcterms:<http://purl.org/dc/terms/> ");
		queryString.append("  SELECT ?lat ?lng");
		queryString.append(" WHERE { " + term + "  geo:lat ?lat. ");
		queryString.append(term + "  geo:long ?lng.");
		queryString.append("} LIMIT 1");


		Couple<String, String> latLng = new Couple<String, String>("", "");
		try{
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service, queryString.toString());
			ResultSet results = queryExecution.execSelect();

			while (results.hasNext()) {
				QuerySolution solution = results.next();
				String lat = String.valueOf(solution.getLiteral("lat").getFloat());
				String lng = String.valueOf(solution.getLiteral("lng").getFloat());
				latLng = new Couple<String, String>(lat, lng);
			}
		}catch(HttpException e){
			logger.error("Http Exception occurred");
			return null;
		}catch(Exception e ){
			logger.error("Exception occurred");
			return null;
		}

		return latLng;
	}

	public List<Venue> textQueryDbpedia(String dbpediaUri) {

		System.out.println("textQueryDbpedia------"+dbpediaUri+"------");


		String ontology_service = this.dbpediaEndpoint;
		String endpoint = "otee:Endpoints";
		String endpointsSparql = PREFIX
				+ "select distinct  ?opera ?museum "
				+ "where { "
				+ "?opera dbpedia-owl:museum ?museum. "
				+ "?museum dbp-prop:location "+ "<" + dbpediaUri + ">" 
				+ "  } LIMIT 200";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = queryExecution.execSelect();

		List<Venue> retrievedObjects = new ArrayList<Venue>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String title = solution.get("opera").toString();
			String uri = solution.get("museum").toString();
			// String mediaUrl = solution.get("mediaURL").toString();
			Venue retrievedObject = new Venue();
			retrievedObject.setName_fq(title);
			retrievedObject.setLink_fq(uri);
			// retrievedObject.setMediaUrl(mediaUrl);
			retrievedObjects.add(retrievedObject);
		}

		return retrievedObjects;
	}

	@Deprecated
	public String queryDbpediaForImageUrl(String dbpediaUri) {

		System.out.println("queryDbpediaForImageUrl------"+dbpediaUri+"------");



		String ontology_service = dbpediaEndpoint;
		String endpoint = "otee:Endpoints";
		String endpointsSparql = PREFIX 
				+ "select distinct ?mediaUrl where{ "
				+ dbpediaUri + " dbpedia-owl:thumbnail ?mediaUrl. } LIMIT 1";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = queryExecution.execSelect();

		List<Venue> retrievedObjects = new ArrayList<Venue>();
		String mediaUrl = "";
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			mediaUrl = solution.get("mediaUrl").toString();
		}

		return mediaUrl;
	}

	/**
	 * Given a dbpedia uri, extracts extra infos. At the moment the extra infos
	 * are the mediaUrl and the external wikipedia link
	 **/
	public static Map<String, String> queryDbpediaForExtraInfo(String dbpediaUri) {

		System.out.println("queryDbpediaForExtraInfo------"+dbpediaUri+"------");

		Map<String, String> dbpediaAttribute2value = new HashMap<String, String>();
		String ontology_service =  "http://dbpedia.org/sparql" /*this.dbpediaEndpoint*/;

		String endpoint = "otee:Endpoints";
		String endpointsSparql = PREFIX
				+ "select distinct ?mediaUrl ?externalLink where{ "
				+ dbpediaUri + " dbpedia-owl:thumbnail ?mediaUrl; foaf:isPrimaryTopicOf ?externalLink.} LIMIT 1";


		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				endpointsSparql);
		ResultSet results = queryExecution.execSelect();

		String mediaUrl = "";
		String externalLink = "";
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			mediaUrl = solution.get("mediaUrl").toString();
			externalLink = solution.get("externalLink").toString();
			dbpediaAttribute2value.put("mediaUrl", mediaUrl);
			dbpediaAttribute2value.put("externalLink", externalLink);
		}

		return dbpediaAttribute2value;
	}

	public static List<String> conceptQuery(String cm, String cx) {

		List<String> extractedConcepts = new ArrayList<>();
		List<String> heritageTypes = new ArrayList<>();

		System.out.println("QUERY 1");


		heritageTypes.add("http://dbpedia.org/ontology/Abbey");
		heritageTypes.add("http://dbpedia.org/ontology/AmusementParkAttraction");
		heritageTypes.add("http://dbpedia.org/ontology/Archeologist");
		heritageTypes.add("http://dbpedia.org/ontology/Archipelago");
		heritageTypes.add("http://dbpedia.org/ontology/Architect");
		heritageTypes.add("http://dbpedia.org/ontology/ArchitecturalStructure");
		heritageTypes.add("http://dbpedia.org/ontology/Artist");
		heritageTypes.add("http://dbpedia.org/ontology/Artwork");
		heritageTypes.add("http://dbpedia.org/ontology/Book");
		heritageTypes.add("http://dbpedia.org/ontology/Building");
		heritageTypes.add("http://dbpedia.org/ontology/Castle");
		heritageTypes.add("http://dbpedia.org/ontology/Cave");
		heritageTypes.add("http://dbpedia.org/ontology/Church");
		heritageTypes.add("http://dbpedia.org/ontology/City");
		heritageTypes.add("http://dbpedia.org/ontology/Country");
		heritageTypes.add("http://dbpedia.org/ontology/Glacier");
		heritageTypes.add("http://dbpedia.org/ontology/Historian");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricBuilding");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricPlace");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricalPeriod");
		heritageTypes.add("http://dbpedia.org/ontology/Island");
		heritageTypes.add("http://dbpedia.org/ontology/Lake");
		heritageTypes.add("http://dbpedia.org/ontology/Locality");
		heritageTypes.add("http://dbpedia.org/ontology/Monument");
		heritageTypes.add("http://dbpedia.org/ontology/Mosque");
		heritageTypes.add("http://dbpedia.org/ontology/Mountain");
		heritageTypes.add("http://dbpedia.org/ontology/Museum");
		heritageTypes.add("http://dbpedia.org/ontology/MusicalWork");
		heritageTypes.add("http://dbpedia.org/ontology/Opera");
		heritageTypes.add("http://dbpedia.org/ontology/Painter");
		heritageTypes.add("http://dbpedia.org/ontology/Painting");
		heritageTypes.add("http://dbpedia.org/ontology/Park");
		heritageTypes.add("http://dbpedia.org/ontology/Photographer");
		heritageTypes.add("http://dbpedia.org/ontology/Rive");
		heritageTypes.add("http://dbpedia.org/ontology/Sculptor");
		heritageTypes.add("http://dbpedia.org/ontology/Sculpture");
		heritageTypes.add("http://dbpedia.org/ontology/Settlement");
		heritageTypes.add("http://dbpedia.org/ontology/Stadium");
		heritageTypes.add("http://dbpedia.org/ontology/Synagogue");
		heritageTypes.add("http://dbpedia.org/ontology/Temple");
		heritageTypes.add("http://dbpedia.org/ontology/Tower");
		heritageTypes.add("http://dbpedia.org/ontology/Volcano");
		heritageTypes.add("http://dbpedia.org/ontology/WaterTower");
		heritageTypes.add("http://dbpedia.org/ontology/WorldHeritageSite");
		heritageTypes.add("http://dbpedia.org/ontology/Work");
		heritageTypes.add("http://dbpedia.org/class/yago/Museum103800563");		
		heritageTypes.add("http://dbpedia.org/class/yago/NationalPark108600992");
		heritageTypes.add("http://dbpedia.org/class/yago/Park108615149");
		heritageTypes.add("http://dbpedia.org/class/yago/Artist109812338");		
		heritageTypes.add("http://dbpedia.org/class/yago/Stadium104295881");
		heritageTypes.add("http://dbpedia.org/class/yago/Structure104341686");	
		heritageTypes.add("http://dbpedia.org/class/yago/Sculpture104157320");	
		heritageTypes.add("http://dbpedia.org/class/yago/Tower104460130");
		heritageTypes.add("http://dbpedia.org/class/yago/Lake109328904");
		heritageTypes.add("http://dbpedia.org/class/yago/YagoPermanentlyLocatedEntity");

		String heritageTypesQueryString = " {" + " ?c a <" + heritageTypes.get(0) + ">.} ";

		for (int i = 1; i < heritageTypes.size(); i++) {
			heritageTypesQueryString += " UNION {" + " ?c a <" + heritageTypes.get(i) + ">.} ";
		}

		String ontology_service = "http://dbpedia.org/sparql";
		//				this.dbpediaEndpoint;

		// String ontology_service = "http://live.dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";



		/**
		 * query 1-5 ---> c is a place 
		 * 7-11 ---> c can be everything 
		 * 12-15 ---> c can be one of a subset of rdf:types 
		 * 16-17 ---> c can be one of a subset if rdf:types and there is even cy 
		 * 18-19 ---> p1=p2 and c can be one of a subset if rdf:types 
		 * 20 ---> p1=p2=dcterms:subject and c can be one of a subset if rdf:types 
		 * 21 - 22 ---> direct mentions
		 * */
		String query1 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " 
				+ cx + " ?p2 ?c. "
				+ "?c rdf:type dbpedia-owl:Place."
				+ "} LIMIT 50 ";


		String query2 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". "
				+ "?c ?p2 " + cx + ". "
				+ "?c rdf:type dbpedia-owl:Place."
				+ "} LIMIT 50 ";


		String query3 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " 
				+ cx + " ?p2 ?c. "
				+ "?c rdf:type dbpedia-owl:Place."
				+ "} LIMIT 50 ";


		String query4 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " 
				+ cm + " ?p2 ?c. "
				+ "?c rdf:type dbpedia-owl:Place."
				+ "} LIMIT 50 ";

		String query5 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. "
				+ "?c ?p2 ?cy. "
				+ "?cy ?p3 " + cx + ". "
				+ "?c rdf:type dbpedia-owl:Place."
				+ "} LIMIT 50 ";

		String query6 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " 
				+ cx + " ?p2 ?c."
				+ "} LIMIT 50 ";

		String query7 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". "
				+ "?c ?p2 " + cx + ". "
				+ "} LIMIT 50 ";


		String query8 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " 
				+ cx + " ?p2 ?c."
				+ "} LIMIT 50 ";


		String query9 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " 
				+ cm + " ?p2 ?c. "
				+ "} LIMIT 50 ";


		String query10 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. "
				+ "?c ?p2 ?cy. "
				+ "?cy ?p3 " + cx + ". "
				+ "} LIMIT 50 ";

		String query11 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " 
				+ cx + " ?p2 ?c. " 
				+ heritageTypesQueryString 
				+ " } LIMIT 50 ";


		String query12 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". "
				+ "?c ?p2 " + cx + ". " 
				+ heritageTypesQueryString + " } LIMIT 50 ";


		String query13 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " 
				+ cx + " ?p2 ?c. " + heritageTypesQueryString + " } LIMIT 50 ";


		String query14 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " 
				+ cm + " ?p2 ?c. "
				+ "} LIMIT 50 ";


		String query15 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. "
				+ "?c ?p2 ?cy. "
				+ "?cy ?p3 " + cx + ". " 
				+ heritageTypesQueryString + " } LIMIT 50 ";



		String query16 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?cy. "
				+ "?cy ?p2 ?c. " 
				+ cx + " ?p3 ?c. " + heritageTypesQueryString + " } LIMIT 50 ";



		String query17 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " dcterms:subject ?cy. "
				+ "?c dcterms:subject ?cy. " + heritageTypesQueryString + " } LIMIT 50 ";


		String query18 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?cy. "
				+ "?c ?p1 ?cy. " + heritageTypesQueryString + " } LIMIT 50 ";


		String query19 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " ?p1 ?cy. " 
				+ cx + " ?p1 ?cy. "
				+ "?c ?p1 ?cy. " 
				+ heritageTypesQueryString + " } LIMIT 50 ";


		String query20 = PREFIX
				+ "select distinct ?c WHERE{ "
				+ cm + " dcterms:subject ?cy. "
				+ cx + " ?p1 ?cy. "
				+ "?c dcterms:subject  ?cy. "
				+ heritageTypesQueryString
				+ " } LIMIT 50 ";

		String query21 = PREFIX
				+ "select distinct ?cx WHERE{ "
				+ cm + " ?p1 " + cx + ". } LIMIT 50 ";


		String query22 = PREFIX
				+ "select distinct ?cx WHERE{ "
				+ cx + " ?p1 " + cm + ". } LIMIT 50 ";

		List<String> allQueries = new ArrayList<>();
		allQueries.add(query1);
		allQueries.add(query2);
		allQueries.add(query3);
		allQueries.add(query4);
		allQueries.add(query5);
		allQueries.add(query6);
		allQueries.add(query7);
		allQueries.add(query8);
		allQueries.add(query9);
		allQueries.add(query10);
		allQueries.add(query11);

		allQueries.add(query12);
		allQueries.add(query13);
		allQueries.add(query14);
		allQueries.add(query15);

		allQueries.add(query16);
		allQueries.add(query17);
		allQueries.add(query18);
		allQueries.add(query19);
		allQueries.add(query20);
		allQueries.add(query21);
		allQueries.add(query22);

		StringBuilder finalResult = new StringBuilder();
		QueryExecution queryExecution = null;
		int i = 1;

		System.out.println("QUERY 2");

		for (String query : allQueries) {

			System.out.println("QUERY 1");

			logger.info(query);

			finalResult.append("QUERY " + i + " : " + query + "\n");
			finalResult.append("==================");
			queryExecution = QueryExecutionFactory.sparqlService(ontology_service,query);
			ResultSet results = queryExecution.execSelect();

			while (results.hasNext()) {
				QuerySolution solution = results.next();
				String concept = "";
				if (solution.get("c") != null) {
					concept = solution.get("c").toString();
				} else if (solution.get("cx") != null) {
					concept = solution.get("cx").toString();
				}
				finalResult.append("\n---------------- \n" + concept);
				extractedConcepts.add(concept);
			}
			finalResult.append("\n\n");
			i++;
		}
		System.out.println(finalResult.toString());
		return extractedConcepts;
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

		String[] category = {"1"};



		JenaManager.retrieveNodeFromLinkedGeoData(41.45, 12.71, 0.5, category);


	}



	public static List<Venue> retrieveNodeFromLinkedGeoData(double lat, double lon, double radius, String[] categories) {



		String artsCategory = "Planetarium, Library, ArtsCentre, ArtGallery, Artwork, Gallery, Observatory, ArtShop, UNESCOWorldHeritage";	
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

		System.out.println(categoriesSet);

		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		List<Venue> result = new ArrayList<>();


		String ontology_service =  "http://linkedgeodata.org/sparql";


		String endpoint = "otee:Endpoints";

		String wheelChair = "";
		String selectWheelChair = "";
		
		if (false)	{
			wheelChair = "OPTIONAL { ?obj lgdo:wheelchair ?wc } .";
			selectWheelChair = "(SAMPLE(?wc) as ?wheel)";
		}


		String query = "{ SELECT ?obj (SAMPLE(?l) as ?label) (SAMPLE(?lat) as ?latitudine) (SAMPLE(?long) as ?longitudine) (SAMPLE(?openHours) as ?open) "+selectWheelChair+" WHERE {"+"\n"
				+"?obj a lgdo:"+categoriesSet.iterator().next()+" ."+"\n"
				+"?obj"+"\n"
				+"rdfs:label ?l ;"+"\n"
				+"g:lat ?lat ;"+"\n"
				+"g:long ?long ."+"\n"
				+"OPTIONAL { ?obj lgdo:cuisine ?cuisine } ."+"\n"
				+"OPTIONAL { ?obj lgdo:opening_hours ?openHours } ."+"\n"
				+wheelChair+"\n"
				+ "FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
				+"} "+"\n"
				+"GROUP BY ?obj"+"\n"
				+"LIMIT 0}";
		
		for (Iterator<String> it = categoriesSet.iterator(); it.hasNext(); ) {
			
			query += "UNION "+"\n"+"{ SELECT ?obj (SAMPLE(?l) as ?label) (SAMPLE(?lat) as ?latitudine) (SAMPLE(?long) as ?longitudine) (SAMPLE(?openHours) as ?open) "+selectWheelChair+" WHERE {"
					+"?obj a lgdo:"+it.next()+" ."+"\n"
					+"?obj"+"\n"
					+"rdfs:label ?l ;"+"\n"
					+"g:lat ?lat ;"+"\n"
					+"g:long ?long ."+"\n"
					+"OPTIONAL { ?obj lgdo:cuisine ?cuisine } ."+"\n"
					+"OPTIONAL { ?obj lgdo:opening_hours ?openHours } ."+"\n"
					+wheelChair+"\n"
					+ "FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"+"\n"
					+"} "+"\n"
					+"GROUP BY ?obj"+"\n"
					+"LIMIT 10}";
		}

			System.out.println(query);

		String queryLGD = "Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
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


		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				queryLGD);


		//		System.out.println(endpointsSparqlOSM);

		System.out.println(queryExecution.getQuery().toString());






		//		ResultSet results = queryExecution.execSelect();

		ResultSet results = queryExecution.execSelect();

		//		ResultSetFormatter.out(System.out, results);
		//				 try {
		//			            ResultSet results2 = queryExecution.execSelect();
		////			            ResultSetFormatter.out(System.out, results, query);
		//			            ResultSetFormatter.out(System.out, results2);
		//			        } catch (Exception ex) {
		//			        	
		//			            System.out.println(ex.getMessage());
		//			        } finally {
		//			            queryExecution.close();
		//			        }




		while (results.hasNext()) {



			Venue obj = new Venue();

			QuerySolution solution = results.next();
			String lat_s = solution.get("latitudine").toString().split("\\^")[0];
			String long_s = solution.get("longitudine").toString().split("\\^")[0];
			String label = solution.get("label").toString().split("\\@")[0];
			
			Flickr flickr = new Flickr("f9ab674ce9f9deb95de6cef1dae510a1", "3fa8105f2df6bbf2", new REST());
			
			
			System.out.println(lat_s);
			System.out.println(long_s);
			
			
//			String[] labelWords = label.split("\\ ");
			
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
	        searchParams.setSort(SearchParameters.RELEVANCE);
	        
//	        searchParams.setLatitude(lat_s);
//	        
//	        searchParams.setLongitude(long_s);
	        
	        PhotoList<Photo> list = new PhotoList<>();
			
			try {
				list = flickr.getPhotosInterface().search(searchParams, 10, 1);
			} catch (FlickrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
//			System.out.println(places.get(0).);
			//			System.out.println("\n");
			//			System.out.println(lat_s);
			System.out.println(label);
			System.out.println("\n");
			System.out.println(solution.get("obj").toString().split("http://linkedgeodata.org/triplify/node")[1]);
			//			System.out.println("\n");

			if (solution.get("link")!=null) {
				obj.setExternalLink(solution.get("link").toString());

			}

			if (!list.isEmpty()) {
				obj.setMediaUrl(list.get(0).getLargeUrl());
			}

			if (solution.get("museumDescription")!=null) {
				obj.setMediaUrl(solution.get("museumDescription").toString().split("\\@")[0]);
			}

			if (solution.get("creatoreLabel")!=null) {
				obj.setMediaUrl(solution.get("creatoreLabel").toString().split("\\@")[0]);
			}

			//			String wiki = solution.get("homepage").toString();		


			//			obj = getUri(getWikiPage(wiki), obj);




			//			String label = getLabel(solution.get("museumLabel").toString());
			//			obj.setName_fq(label.replaceAll("\'", ""));
			//			obj.setCategory_fq(solution.get("categoria_principale_dell_argomentoLabel").toString().split("\\@")[0]);
			obj.setId(Long.parseLong((solution.get("obj").toString().split("http://linkedgeodata.org/triplify/node")[1])));
			obj.setProvider("LinkedGeoData");		 
			obj.setName_fq(label);
			obj.setWhy("Close place");
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);

			//			logger.info("NAME FROM LINKED GEO DATA\t"+ label);
			result.add(obj);

			//			if(result.size() == 5){
			//				break;
			//			}
		}




		System.out.println(result.size());



		return result;
	}


	private static String getLabel(String node){

		String result="";
		String ontology_service =  "http://linkedgeodata.org/sparql" /*this.dbpediaEndpoint*/;

		String endpointsSparql =  PREFIX_LOD
				+ "select * where{ "
				+ "<"+node+"> <http://www.w3.org/2000/01/rdf-schema#label> ?label."
				+ "FILTER(LANGMATCHES(LANG(?label), \"it\"))."
				+ "}";

		String endpointsSparql2 =  PREFIX_LOD
				+ "select * where{ "
				+ "<"+node+"> <http://www.w3.org/2000/01/rdf-schema#label> ?label." 
				+ "} LIMIT 1";


		ResultSet results = createResultSet(ontology_service,endpointsSparql);


		while (results.hasNext()) {
			QuerySolution solution = results.next();
			result = solution.get("label").toString();
		}

		if(result==null || result.isEmpty()){
			results = createResultSet(ontology_service,endpointsSparql2);
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				result = solution.get("label").toString();
			}
		}

		return result;

	}

	private static Venue getUri(String node, Venue obj){

		String result="";
		String ontology_service = "http://dbpedia.org/sparql";

		String endpointsSparql =  PREFIX 
				+ "select distinct ?obj ?mediaUrl where { "
				+  "?obj foaf:isPrimaryTopicOf <"+node+"> ."
				+ "?obj dbpedia-owl:thumbnail ?mediaUrl. "
				+ "} LIMIT 1";

		ResultSet results = createResultSet(ontology_service,endpointsSparql);


		while (results.hasNext()) {
			QuerySolution solution = results.next();
			result = solution.get("obj").toString();
			obj.setLink_fq(result);
			result = solution.get("mediaUrl").toString();
			obj.setMediaUrl(result);
		}



		return obj;

	}


	private static ResultSet createResultSet(String s, String s2){

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(s,s2);
		return queryExecution.execSelect();

	}

	private static String getWikiPage(String result){
		String  s=null;
		if(result!=null && !result.isEmpty()){
			result = result.substring(3).replace(" ", "_");
			try {
				s ="https://it.wikipedia.org/wiki/" +  URLEncoder.encode(result, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	
	

	public static Collection<? extends Venue> retrieveReccommendFromDBPedia(double lat, double lng, double d) {

		List<String> heritageTypes = new ArrayList<>();

		heritageTypes.add("http://dbpedia.org/ontology/Abbey");
		heritageTypes.add("http://dbpedia.org/ontology/AmusementParkAttraction");
		heritageTypes.add("http://dbpedia.org/ontology/Archeologist");
		heritageTypes.add("http://dbpedia.org/ontology/Archipelago");
		heritageTypes.add("http://dbpedia.org/ontology/Architect");
		heritageTypes.add("http://dbpedia.org/ontology/ArchitecturalStructure");
		heritageTypes.add("http://dbpedia.org/ontology/Artist");
		heritageTypes.add("http://dbpedia.org/ontology/Artwork");
		heritageTypes.add("http://dbpedia.org/ontology/Book");
		heritageTypes.add("http://dbpedia.org/ontology/Building");
		heritageTypes.add("http://dbpedia.org/ontology/Castle");
		heritageTypes.add("http://dbpedia.org/ontology/Cave");
		heritageTypes.add("http://dbpedia.org/ontology/Church");
		heritageTypes.add("http://dbpedia.org/ontology/City");
		heritageTypes.add("http://dbpedia.org/ontology/Country");
		heritageTypes.add("http://dbpedia.org/ontology/Glacier");
		heritageTypes.add("http://dbpedia.org/ontology/Historian");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricBuilding");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricPlace");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricalPeriod");
		heritageTypes.add("http://dbpedia.org/ontology/Island");
		heritageTypes.add("http://dbpedia.org/ontology/Lake");
		heritageTypes.add("http://dbpedia.org/ontology/Locality");
		heritageTypes.add("http://dbpedia.org/ontology/Monument");
		heritageTypes.add("http://dbpedia.org/ontology/Mosque");
		heritageTypes.add("http://dbpedia.org/ontology/Mountain");
		heritageTypes.add("http://dbpedia.org/ontology/Museum");
		heritageTypes.add("http://dbpedia.org/ontology/MusicalWork");
		heritageTypes.add("http://dbpedia.org/ontology/Opera");
		heritageTypes.add("http://dbpedia.org/ontology/Painter");
		heritageTypes.add("http://dbpedia.org/ontology/Painting");
		heritageTypes.add("http://dbpedia.org/ontology/Park");
		heritageTypes.add("http://dbpedia.org/ontology/Photographer");
		heritageTypes.add("http://dbpedia.org/ontology/Rive");
		heritageTypes.add("http://dbpedia.org/ontology/Sculptor");
		heritageTypes.add("http://dbpedia.org/ontology/Sculpture");
		heritageTypes.add("http://dbpedia.org/ontology/Settlement");
		heritageTypes.add("http://dbpedia.org/ontology/Stadium");
		heritageTypes.add("http://dbpedia.org/ontology/Synagogue");
		heritageTypes.add("http://dbpedia.org/ontology/Temple");
		heritageTypes.add("http://dbpedia.org/ontology/Tower");
		heritageTypes.add("http://dbpedia.org/ontology/Volcano");
		heritageTypes.add("http://dbpedia.org/ontology/WaterTower");
		heritageTypes.add("http://dbpedia.org/ontology/WorldHeritageSite");
		heritageTypes.add("http://dbpedia.org/ontology/Work");
		heritageTypes.add("http://dbpedia.org/class/yago/Museum103800563");		
		heritageTypes.add("http://dbpedia.org/class/yago/NationalPark108600992");
		heritageTypes.add("http://dbpedia.org/class/yago/Park108615149");
		heritageTypes.add("http://dbpedia.org/class/yago/Artist109812338");		
		heritageTypes.add("http://dbpedia.org/class/yago/Stadium104295881");
		heritageTypes.add("http://dbpedia.org/class/yago/Structure104341686");	
		heritageTypes.add("http://dbpedia.org/class/yago/Sculpture104157320");	
		heritageTypes.add("http://dbpedia.org/class/yago/Tower104460130");
		heritageTypes.add("http://dbpedia.org/class/yago/Lake109328904");
		heritageTypes.add("http://dbpedia.org/class/yago/YagoPermanentlyLocatedEntity");

		String heritageTypesQueryString = " {" + " ?c a <" + heritageTypes.get(0) + ">.} ";

		for (int i = 1; i < heritageTypes.size(); i++) {
			heritageTypesQueryString += " UNION {" + " ?c a <" + heritageTypes.get(i) + ">.} ";
		}

		double  lat1 = lat - d,
				lat2 = lat + d,
				lon1 = lng - d,
				lon2 = lng + d;

		List<Venue> result = new ArrayList<>();


		String ontology_service = "http://dbpedia.org/sparql";
		//				this.dbpediaEndpoint;

		// String ontology_service = "http://live.dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";

		String query1 = PREFIX
				+ "select  distinct ?obj ?lat ?long ?mediaUrl ?externalLink where{ "
				+ heritageTypesQueryString 
				+ "?obj geo:lat ?lat."
				+ "?obj geo:long ?long."
				+ "?obj dbpedia-owl:thumbnail ?mediaUrl. "
				+ "?obj foaf:isPrimaryTopicOf ?externalLink."
				+ "FILTER(?lat >"+lat1+" && ?lat<="+lat2+" && ?long>"+lon1+" && ?long<="+lon2+")"
				+ "} LIMIT 20";

		QueryExecution queryExecution = null;


		System.out.println(query1);

		queryExecution = QueryExecutionFactory.sparqlService(ontology_service, query1);
		ResultSet results = queryExecution.execSelect();

		while (results.hasNext()) {

			Venue obj = new Venue();

			QuerySolution solution = results.next();
			String lat_s = solution.get("lat").toString();
			String long_s = solution.get("long").toString();
			String mediaUrl = solution.get("mediaUrl").toString();
			String externalLink = solution.get("externalLink").toString();
			String objj  = solution.get("obj").toString();

			obj.setLink_fq(objj);
			obj.setExternalLink(externalLink);
			obj.setMediaUrl(mediaUrl);
			obj.setProvider("DBPedia");
			obj.setWhy("Close place");
			obj.setLatitude(lat_s.substring(0,7).replaceAll("\\^", ""));	 
			obj.setLongitude(long_s.substring(0,7).replaceAll("\\^", ""));


			result.add(obj);

			if(result.size() == 5){
				break;
			}
		}

		return result;
	}






}
