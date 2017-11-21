package logic.router;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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

import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import model.Object;
import model.Singer;

public class JenaManagerForTraks implements JenaManager{

	private final static Logger logger = LoggerFactory.getLogger(JenaManagerForTraks.class);

	private final static String prefix = "PREFIX wd: <http://www.wikidata.org/entity/>"+"\n"
			+"PREFIX wdt: <http://www.wikidata.org/prop/direct/>"+"\n"
			+"PREFIX wikibase: <http://wikiba.se/ontology#>"+"\n"
			+"PREFIX p: <http://www.wikidata.org/prop/>"+"\n"
			+"PREFIX ps: <http://www.wikidata.org/prop/statement/>"+"\n"
			+"PREFIX pq: <http://www.wikidata.org/prop/qualifier/>"+"\n"
			+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n"
			+"PREFIX bd: <http://www.bigdata.com/rdf#>"+"\n"
			+"PREFIX geo: <http://www.opengis.net/ont/geosparql#>"+"\n"
			+"PREFIX psv: <http://www.wikidata.org/prop/statement/value/>"+"\n"
			+"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+"\n";

	private final static String ontology_serviceTrack =  "https://query.wikidata.org/sparql";

	public Map<Long, Object> retriveNodes(double lat, double lon, double radius)	{


		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;

		Map<Long, Object> singerResult = new HashMap<>();

		String queryTrack = prefix
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

		while (results.hasNext()) {



			QuerySolution solution = results.next();


			String lat_s = solution.get("lat").toString();
			String long_s = solution.get("long").toString();
			long id = Long.parseLong((solution.get("artista").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String label = solution.get("artistaLabel").toString().split("\\@")[0];
			String genre = solution.get("genereLabel").toString().split("\\@")[0];

			label = label.replace("\\\"","");

			String key = null; //this is the key used in the Last.fm API 

			Properties prop = new Properties();
			InputStream input = null;

			try {



				prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
				// get the property value and print it out

				key = prop.getProperty("LAST.FM_KEY");

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

			Collection<User> fan = Artist.getTopFans(label, key);



			Singer obj = new Singer(fan.size());

			obj.setName(label);
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			obj.setId(id);
			obj.getGenres().add(genre);
			logger.info("NAME FROM LAST.FM AND WIKI DATA\t"+ label);





			if (!singerResult.containsKey(id))	{


				Collection<Track> topTracks = Artist.getTopTracks(label, key);

				System.out.println("Top Tracks for "+label+":");

				Collection<Album> albums = Artist.getTopAlbums(label, key);

				List<Album> albums2 = new ArrayList<>(albums);

				if (albums2.size()>0)	{

					String image = albums2.get(0).getImageURL(ImageSize.LARGE);

					obj.setImage(image);

				}
				
				

				for (Track track : topTracks) {

					System.out.println(track.getName());

					obj.getSong().add(track.getName()); 
					obj.setPopularity(track.getPlaycount()); 
					obj.setExternalLink(track.getUrl()); 
					
					if (obj.getImage()==null)	{
						obj.setImage(track.getImageURL(ImageSize.LARGE));
					}

				}

				singerResult.put(id, obj);
			}

			else {
				((Singer) singerResult.get(id)).getGenres().add(genre);

			}

		}

		return singerResult;

	}

	public static void main(String[] args)	{

		JenaManagerForTraks jTraks = new JenaManagerForTraks();

		Map<Long, Object> userTracks = jTraks.retriveNodes(41.89, 12.49, 0.1);

		Singer singer = (Singer) Singer.weightedChoice(userTracks);

		System.out.println(singer.getPopularity());

	}

}
