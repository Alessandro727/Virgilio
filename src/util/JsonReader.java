package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.neo4j.cypher.internal.compiler.v2_3.codegen.setStaticField;

import logic.router.JenaManagerForPlace;

import org.jsoup.Jsoup;

import model.Venue;


@SuppressWarnings("deprecation")
public class JsonReader {


	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			System.out.println(jsonText);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static String getPosterUrl(String link) throws IOException, JSONException {
		String tmdbApi = null;
		Properties prop = new Properties();
		InputStream input = null;
		try {

			prop.load(Utilities.class.getClassLoader().getResourceAsStream("config.properties"));
			// get the property value and print it out

			tmdbApi = prop.getProperty("TMDB_API");

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
		JSONObject json = readJsonFromUrl("https://api.themoviedb.org/3/movie/"+link+"?api_key="+tmdbApi+"&language=en-US");
		String posterUrl = json.getString("poster_path");
		posterUrl = "http://image.tmdb.org/t/p/w185"+posterUrl;
		return posterUrl;
	}


	public static String getBookImage(String imdbId) throws IOException	{

		try {

			String link = "http://covers.openlibrary.org/b/isbn/"+imdbId+"-L.jpg?default=false";
			System.out.println(link);

			@SuppressWarnings("unused")
			Response resultImageResponse = Jsoup.connect(link)
			.ignoreContentType(true).execute();

			return link;
		} catch (Exception e) {
			return "not found";
		}

	}

	public static List<Venue> filterClosedVenue(List<Venue> venues, List<String> categories)	{

		List<Venue> openVenue = new ArrayList<>();

		Map<Integer, String> categoryMap = JenaManagerForPlace.createCategoryMap(categories);

		for(Venue venue : venues)	{

			try {
				int k=0;
				for(Integer i : categoryMap.keySet())	{
					if(categoryMap.get(i).contains(venue.getCategory_fq()))	{
						k=i;
					}
				}

				String venueCategoryYelp = getYelpCategory(k);

				String tokenId = null;
				String tokenSecret = null;

				Properties prop = new Properties();
				InputStream input = null;

				try {

					prop.load(Utilities.class.getClassLoader().getResourceAsStream("config.properties"));
					// get the property value and print it out

					tokenId = prop.getProperty("YELP_TOKEN");
					tokenSecret = prop.getProperty("YELP_TOKEN_SECRET");

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


				@SuppressWarnings("resource")
				HttpClient client = new DefaultHttpClient();  
				String getURL = "https://api.yelp.com/v3/businesses/search?latitude="+venue.getLatitude()+"&longitude="+venue.getLongitude()+"&categories="+venueCategoryYelp+"&radius=150";
				HttpGet get = new HttpGet(getURL);
				get.setHeader("Authorization", tokenId+" "+tokenSecret);
				HttpResponse responseGet = client.execute(get);  
				HttpEntity resEntityGet = responseGet.getEntity();  
				InputStream is = resEntityGet.getContent();
				try {
					BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
					String jsonText = readAll(rd);
					//					System.out.println(jsonText);
					JSONObject json = new JSONObject(jsonText);
					for (int i=0; i<json.getJSONArray("businesses").length(); i++)	{


						String jsonName = json.getJSONArray("businesses").getJSONObject(i).get("name").toString();
						//						System.out.println(jsonName);
						String jsonAddress = json.getJSONArray("businesses").getJSONObject(i).getJSONObject("location").get("address1").toString();
						//						System.out.println(jsonAddress);



						if (compareStrings(jsonName, venue.getName_fq())>0.70 )  {
							if(json.getJSONArray("businesses").getJSONObject(i).get("is_closed").toString().equals("false"))		{
								if(!openVenue.contains(venue))	{
									openVenue.add(venue);
								}
							}
						}
						else {
							if(venue.getAddress()!=null)	{
								if(compareStrings(jsonAddress, venue.getAddress())>0.94 )	{
									if(!openVenue.contains(venue))	{
										openVenue.add(venue);
									}
								}
							}

						}

					}

				} finally {
					is.close();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		return openVenue;
	}

	private static String getYelpCategory(int k) {

		switch(k)	{
		case 1: return "museums";
		case 2: return "religiousorgs";
		case 3: return "publicservicesgovt";
		case 4: return "arts";
		case 5: return "arts,active";
		case 6: return "restaurants";
		case 7: return "arts";
		case 8: return "nightlife";
		case 9: return "shopping";
		case 10: return "active";
		default: return null;
		}

	}

	public static double compareStrings(String stringA, String stringB) {
		return StringUtils.getJaroWinklerDistance(stringA, stringB);
	}
	
	public static void main(String[] args) throws JSONException, IOException	{
		
		System.out.println(getPosterUrl("tt0110912"));
		
	}





}