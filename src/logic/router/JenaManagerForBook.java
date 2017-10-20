package logic.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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

import model.Book;

public class JenaManagerForBook {

	private final static Logger logger = LoggerFactory.getLogger(JenaManagerForBook.class);

	private static final String prefix ="PREFIX wd: <http://www.wikidata.org/entity/>"+"\n"
			+"PREFIX wdt: <http://www.wikidata.org/prop/direct/>"+"\n"
			+"PREFIX wikibase: <http://wikiba.se/ontology#>"+"\n"
			+"PREFIX p: <http://www.wikidata.org/prop/>"+"\n"
			+"PREFIX ps: <http://www.wikidata.org/prop/statement/>"+"\n"
			+"PREFIX pq: <http://www.wikidata.org/prop/qualifier/>"+"\n"
			+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
			+"PREFIX bd: <http://www.bigdata.com/rdf#>"+"\n"
			+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>";

	private final static String ontology_serviceBook =  "https://query.wikidata.org/sparql";

	public static Map<Long, Book> retriveBooksNodes(double lat, double lon, double radius)	{


		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		Map<Long, Book> bookResult = new HashMap<>();


		String queryBook = prefix+"\n"

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

		String key = null;
		String secret = null;

		InputStream inputStream = 
				JenaManagerForBook.class.getClassLoader().getResourceAsStream("config.txt");
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

			Book obj = new Book();

			QuerySolution solution = results.next();

			String point = solution.get("_coord").toString().split("\\^")[0].split("\\(")[1].split("\\)")[0];
			String lat_s = point.split(" ")[1];
			String long_s = point.split(" ")[0];
			long id = Long.parseLong((solution.get("item").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String autor = solution.get("auteurLabel").toString().split("\\@")[0];
			String label = solution.get("itemLabel").toString().split("\\@")[0];
			String genre = solution.get("genreLabel").toString().split("\\@")[0];

			Flickr flickr = new Flickr(key, secret, new REST());

			label = label.replace("\\\"","");
			autor = autor.replace("\\\"","");

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

}
