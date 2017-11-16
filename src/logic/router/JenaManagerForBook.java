package logic.router;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import model.Object;

public class JenaManagerForBook implements JenaManager{

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

	public  Map<Long, Object> retriveNodes(double lat, double lon, double radius)	{


		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		Map<Long, Object> bookResult = new HashMap<>();


		String queryBook = prefix+"\n"

				+"SELECT DISTINCT ?item ?itemLabel  ?auteurLabel ?placeLabel (SAMPLE(?coord) AS ?_coord) (SAMPLE(?linkcount) AS ?link_count) (SAMPLE(?connection) AS ?conn) ?genreLabel (SAMPLE(?image) AS ?img) ?ISBN_13 WHERE {"+"\n"
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
	  			+"?item wdt:P212 ?ISBN_13."+"\n"

				+"}"+"\n"
				+"GROUP BY ?item ?itemLabel ?auteurLabel ?placeLabel ?genreLabel ?ISBN_13"+"\n"
				+"ORDER BY DESC(?link_count)";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_serviceBook,
				queryBook);



		System.out.println(queryExecution.getQuery().toString());

		ResultSet results = queryExecution.execSelect();

		String key = null;
		String secret = null;


		Properties prop = new Properties();
		InputStream input = null;

		try {



			prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
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

			QuerySolution solution = results.next();


			String point = solution.get("_coord").toString().split("\\^")[0].split("\\(")[1].split("\\)")[0];
			String lat_s = point.split(" ")[1];
			String long_s = point.split(" ")[0];
			long id = Long.parseLong((solution.get("item").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String autor = solution.get("auteurLabel").toString().split("\\@")[0];
			String label = solution.get("itemLabel").toString().split("\\@")[0];
			String genre = solution.get("genreLabel").toString().split("\\@")[0];
			int popularity = Integer.parseInt(solution.get("link_count").toString().split("\\^")[0]);
			String isbn = solution.get("ISBN_13").toString().replace("-", "");
			System.out.println(isbn);

			Book obj = new Book(popularity);

			Flickr flickr = new Flickr(key, secret, new REST());

			if (solution.get("img")!=null)	{
				String img = solution.get("img").toString();
				obj.setImage(img);
			}

			obj.setISBN(isbn);

			label = label.replace("\\\"","");
			autor = autor.replace("\\\"","");

			obj.setName(label);
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			obj.setId(id);
			obj.setCreator(autor);
			obj.getGenres().add(genre);
			obj.setPopularity(popularity);

			if(solution.get("conn")!=null)	{
				String link = solution.get("conn").toString();
				obj.setExternalLink(link);
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

//			try {
//				list = flickr.getPhotosInterface().search(searchParams, 10, 1);
//			} catch (FlickrException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			if (obj.getImage()==null)
				if (!list.isEmpty()) {
					obj.setImage(list.get(0).getLargeUrl());
				}

			logger.info("NAME FROM WIKI DATA\t"+ label);

			if (!bookResult.containsKey(id))
				bookResult.put(id, obj);
			else {
				((Book) bookResult.get(id)).getGenres().add(genre);

			}


		}

		return bookResult;

	}

	public static void main(String[] args) {

		List<String> cat = new ArrayList<>();

		cat.add("3");
		JenaManagerForBook jBook = new JenaManagerForBook();

		Map<Long, Object> userBooks = jBook.retriveNodes(41.89, 12.49, 0.1);

		Book book =  (Book) Book.weightedChoice(userBooks);

		System.out.println(book.getImage());
	}


}
