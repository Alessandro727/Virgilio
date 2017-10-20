package logic.router;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Movie;

public class JenaManagerForMovies {
	
	private final static Logger logger = LoggerFactory.getLogger(JenaManagerForMovies.class);
	
	private final static String prefixes = "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"
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
			+"PREFIX wd: <http://www.wikidata.org/entity/>"+"\n";
	
	private final static String ontology_serviceMovie =  "https://query.wikidata.org/sparql";

	public static Map<Long, Movie> retriveNodes(double lat, double lon, double radius)	{


		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		Map<Long, Movie> movieResult = new HashMap<>();


		String queryMovie = prefixes
				+"SELECT DISTINCT ?item ?itemLabel (SAMPLE(?coord) AS ?_coord) (SAMPLE(?linkcount) AS ?link_count) (SAMPLE(?image) AS ?img) ?officialWebsite ?genreLabel ?id_IMDb ?registaLabel ?immagine WHERE {"+"\n"
				+"?item wdt:P31 wd:Q11424."+"\n"
				+"OPTIONAL { ?item wdt:P18 ?image. }"+"\n"
				+"?item wdt:P915 ?place."+"\n"
				+"?item wikibase:sitelinks ?linkcount."+"\n"
				+"SERVICE wikibase:label { bd:serviceParam wikibase:language \"it,en,fr,ar,be,bg,bn,ca,cs,da,de,el,es,et,fa,fi,he,hi,hu,hy,id,ja,jv,ko,nb,nl,eo,pa,pl,pt,ro,ru,sh,sk,sr,sv,sw,te,th,tr,uk,yue,vec,vi,zh\". }"+"\n"
				+"MINUS { ?item wdt:P840 wd:Q933. }"+"\n"
				+"SERVICE wikibase:box {"+"\n"
				+"?place wdt:P625 ?coord."+"\n"
				+"bd:serviceParam wikibase:cornerWest \"Point("+lon1+" "+lat1+")\"^^geo:wktLiteral ."+"\n"
				+"bd:serviceParam wikibase:cornerEast \"Point("+lon2+" "+lat2+")\"^^geo:wktLiteral ."+"\n"
				+"}"+"\n"
				+"OPTIONAL { ?item wdt:P856 ?officialWebsite. }"+"\n"
				+"OPTIONAL { ?item wdt:P345 ?id_IMDb. }"+"\n"
				+"?item wdt:P136 ?genre. "+"\n"
				+"OPTIONAL { ?item wdt:P57 ?regista. }"+"\n"
				+"OPTIONAL { ?item wdt:P18 ?immagine. }"+"\n"
				+"}"+"\n"
				+"GROUP BY ?item ?itemLabel ?auteurLabel ?genreLabel ?officialWebsite ?id_IMDb ?registaLabel ?immagine"+"\n"
				+"ORDER BY DESC(?link_count)";
				

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_serviceMovie,
				queryMovie);



		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();

		while (results.hasNext()) {

			Movie obj = new Movie();

			QuerySolution solution = results.next();


			String point = solution.get("_coord").toString().split("\\^")[0].split("\\(")[1].split("\\)")[0];
			String lat_s = point.split(" ")[1];
			String long_s = point.split(" ")[0];
			long id = Long.parseLong((solution.get("item").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String label = solution.get("itemLabel").toString().split("\\@")[0];
			String genre = solution.get("genreLabel").toString().split("\\@")[0];
			String director = solution.get("registaLabel").toString().split("\\@")[0];
			
			if (solution.get("img")!=null)	{
				String image = solution.get("img").toString();
				obj.setImage(image);
			}

			label = label.replace("\\\"","");

			obj.setName(label);
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			obj.setId(id);
			obj.setDirector(director);
			obj.getGenres().add(genre);
			
			
			String link = null;
			
			if (solution.get("officialWebsite")!=null)	{
				link = solution.get("officialWebsite").toString();
			}
			else	{
				link = solution.get("id_IMDb").toString();
				link = "https://tools.wmflabs.org/wikidata-externalid-url/?p=345&url_prefix=http://www.imdb.com/&id="+link;
			}
			
			obj.setExternalLink(link);

			if (!movieResult.containsKey(id))	{
				//this is the key used in the Last.fm API examples

				movieResult.put(id, obj);
			}

			else {
				movieResult.get(id).getGenres().add(genre);

			}
			
			logger.info("NAME FROM IMDb AND WIKI DATA\t"+ label);

		}

		return movieResult;

	}


}
