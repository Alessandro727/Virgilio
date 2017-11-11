package logic.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Track;
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

			Singer obj = new Singer();

			QuerySolution solution = results.next();


			String lat_s = solution.get("lat").toString();
			String long_s = solution.get("long").toString();
			long id = Long.parseLong((solution.get("artista").toString().split("http://www.wikidata.org/entity/Q")[1]));
			String label = solution.get("artistaLabel").toString().split("\\@")[0];
			String genre = solution.get("genereLabel").toString().split("\\@")[0];

			label = label.replace("\\\"","");

			obj.setName(label);
			obj.setLatitude(lat_s);	
			obj.setLongitude(long_s);
			obj.setId(id);

			obj.getGenres().add(genre);
			logger.info("NAME FROM LAST.FM AND WIKI DATA\t"+ label);

			String key = null; //this is the key used in the Last.fm API 

			InputStream inputStream = 
					JenaManagerForTraks.class.getClassLoader().getResourceAsStream("config.txt");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream ));

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
				((Singer) singerResult.get(id)).getGenres().add(genre);

			}

		}

		return singerResult;

	}

}
