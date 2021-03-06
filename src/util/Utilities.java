package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import logic.router.Node;
import model.Venue;
import net.aksingh.owmjapis.AbstractWeather.Weather;
import net.aksingh.owmjapis.HourlyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;
import socialAndServices.Google;

public class Utilities {
	
	public static LinkedList<Venue> orderVenuesByDistance(LinkedList<Venue> list, String lat, String lng) {
		Map<Double, Venue> map = new HashMap<Double, Venue>();
		LinkedList<Venue> venues = new LinkedList<Venue>();
		double distance;
		
		for (Venue v: list) {
			distance = GeoLocation.getDistanceBetweenTwoPoints(lat, lng, v.getLatitude(), v.getLongitude());
			map.put(distance, v);
		}
		
		
		for (double key: map.keySet())
			venues.add(map.get(key));
		
		return venues;
	}
	
	
	
	public static boolean isOpen(Venue venue) {
		if (venue.getFoursquare_id() == null)
			return true;
		
		String url;
        JSONObject jsonObject;
        JSONObject venueJsonObject;
        String isOpen = null;
        
        String clientID = null;
        String clientSecret = null;

        Properties prop = new Properties();
		InputStream input = null;

		try {

		

			prop.load(Utilities.class.getClassLoader().getResourceAsStream("config.properties"));
			// get the property value and print it out

			clientID = prop.getProperty("FQ_CLIENT_ID");
			clientSecret = prop.getProperty("FQ_CLIENT_SECRET");

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
        
        try {        	
        	url = "https://api.foursquare.com/v2/venues/"
            	+ venue.getFoursquare_id()
            	+ "?client_id="+clientID
            	+ "&client_secret="+clientSecret
            	+ "&v=20120609";        			
        	jsonObject = Utilities.getJSONObjectFromURL(url);
        	if (jsonObject == null)
        		return true;
        	
        	venueJsonObject = jsonObject.getJSONObject("response").getJSONObject("venue");
        	        
        	if (venueJsonObject.has("hours"))
        		isOpen = venueJsonObject.getJSONObject("hours").get("isOpen").toString();
        	else
        		isOpen = "true";        	
        } catch (JSONException e) {
        	e.printStackTrace();
		}
        
        if (isOpen.equals("false")) {
    		System.out.println(venue.getName_fq() + " chiuso.");
    		return false;
    	} else
    		return true;
	}
	
	
	
	
	public static List<Venue> getFirst_N_ElementFromMap(Map<Integer, List<Venue>> map, int n) {
		if (map.size() < 1)
			return null;
		
		List<Venue> venues = new ArrayList<Venue>();
					
		int addedVenues = 0;
		
		for(int i: map.keySet()) {
			if (addedVenues >= n)
				break;
			for (Venue v: map.get(i)) {
				if (addedVenues >= n)
					break;
				venues.add(v);
				addedVenues++;
			}			
		}
		
		return venues;
	}
	
	
	public static List<Venue> getFirst_N_ElementFromMapWithOneFood(Map<Integer, List<Venue>> map, int n) {
		if (map.size() < 1)
			return null;
		
		List<Venue> venues = new ArrayList<Venue>();
					
		int addedVenues = 0;
		boolean foodInserted = false;
		
		for(int i: map.keySet()) {
			if (addedVenues >= n)
				break;
			for (Venue v: map.get(i)) {
				if (addedVenues >= n)
					break;
				if (v.getMacro_category().getId() == 5) {
					if (!foodInserted) {
						venues.add(v);
						addedVenues++;
						foodInserted = true;
					}
				} else {
					venues.add(v);
					addedVenues++;
				}
					
				
			}			
		}
		
		return venues;
	}
	
	
	
	/**
	 * 
	 * @param v
	 * @param availableTime
	 * @return	true, se il tempo � sereno e/o nuvoloso, false altrimenti
	 */
	public static boolean isSunny(Venue v, int availableTime) {
		
		String owmKey = null;

		Properties prop = new Properties();
		InputStream input = null;

		try {


			prop.load(Utilities.class.getClassLoader().getResourceAsStream("config.properties"));
			// get the property value and print it out

			owmKey = prop.getProperty("OWM_KEY");
			

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
		
		OpenWeatherMap opw = new OpenWeatherMap(owmKey);
		int result = 0;
		int limit = 2;
		if (availableTime >= 200)
			limit = 3;
		try {
			HourlyForecast fwd = opw.hourlyForecastByCoordinates(Float.valueOf(v.getLatitude()), Float.valueOf(v.getLongitude()));
			System.out.println(fwd.getForecastCount());
			Weather w;
			for (int i=1; i<=limit && i<=fwd.getForecastCount(); i++) {
				w = fwd.getForecastInstance(i).getWeatherInstance(0);
				System.out.println(w.getWeatherCode() + " " + w.getWeatherName() + " " + w.getWeatherDescription());
				switch(w.getWeatherCode()) {
					case 800: case 801: case 802: case 803: case 804:	result ++; break;		// sole e/o nuvole
					
					case 500: case 501: case 502: case 503: case 504:
					case 511: case 520: case 521: case 522: case 532:	result --; break;		// pioggia
					
					case 300: case 301: case 302: case 310: case 311:
					case 312: case 313: case 314: case 321:				result -= 0.5; break;	// pioggiarella
				
					case 200: case 201: case 202: case 210: case 211:
					case 212: case 221: case 230: case 231: case 232:	result--; break;		//fulmini
					
					case 600: case 601: case 602: case 611: case 612:
					case 615: case 616: case 620: case 621: case 622:	result --; break;		// neve
					
					default: break;
				}				
			}		
			/*for (Forecast f:  fwd.getForecast_List()) {
				net.aksingh.java.api.owm.ForecastWeatherData.Forecast.Weather ww = f.getWeather_List().get(0);
				System.out.println(ww.getWeatherCode() + " " + ww.getWeatherName() + " " + ww.getWeatherDescription() + " " + f.getDateTimeText());
				i++;
				if (i==4)
					break;
			}*/			
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		}
		if (result >= 0)
			return true;
		else
			return false;		
	}
	
	
	
	
	
	/**
	 * Copia la lista in una nuova lista e la ritorna
	 * 
	 * @param list	un tour da clonare
	 * @return		una clone di list
	 */
	public static List<Node> cloneNodeList(List<Node> list, boolean cloneSucc) {
		List<Node> clone = new ArrayList<Node>();
		
		Node curr, prev = null;
		
		if (cloneSucc) {			
			for(int i=list.size()-1; i>0; i--) {
				curr = new Node(list.get(i).getVenue());
				curr.SetOutgoingEdge(list.get(i).getOutGoingEdges());
				
				prev = new Node(list.get(i-1).getVenue());
				prev.SetOutgoingEdge(list.get(i-1).getOutGoingEdges());
				
				prev.setSucc(curr);
				prev.setCostToSucc(list.get(i-1).getCostToSucc());
				
				clone.add(0, curr);
			}	
			clone.add(0, prev);
		}
		else {
			for (Node n: list) {
				curr = new Node(n.getVenue());
				curr.SetOutgoingEdge(n.getOutGoingEdges());
				clone.add(curr);
			}
		}					
		
		return clone;
	}
	
	
	
	
	public static JSONObject getJSONObjectFromURL(String s) {
		URL url;
		URLConnection conn;
		InputStreamReader isr;
        StringBuffer sb;
        String jsonString;
        JSONObject jsonObject = null;
        
		try {
			url = new URL(s);
			conn = url.openConnection();
			conn.connect();
			isr = new InputStreamReader(conn.getInputStream());
			sb = new StringBuffer();
			for (int i=0; i!=-1; i=isr.read())   
	            sb.append((char)i);
			jsonString = sb.toString().trim();
	        jsonObject = new JSONObject(jsonString);
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
		} catch (JSONException e) {
			e.printStackTrace();
		}                                                              
        
		return jsonObject;
	}
	
	
	
	public static boolean arrayContainsString(String[] array, String s) {
		boolean contains = false;
		int i=0;
		while(!contains && i<array.length) {
			if (array[i].equals(s))
				contains = true;
			i++;
		}		
		return contains;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	public static boolean isFoodTime(String lat, String lon)	{
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("HH:mm:ss");

		Google google = new Google();
		String timezone = google.getTimezoneFromLatitudeAndLongitude(lat, lon);
		df.setTimeZone(TimeZone.getTimeZone(timezone));

		String time = df.format(date);
		return checkTime("11:30:00", "13:30:00", time) || checkTime("19:00:00", "23:00:00", time);
		
	}
	
	public static boolean isNightTime(String lat, String lon)	{
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("HH:mm:ss");

		Google google = new Google();
		String timezone = google.getTimezoneFromLatitudeAndLongitude(lat, lon);
		df.setTimeZone(TimeZone.getTimeZone(timezone));

		String time = df.format(date);
		return checkTime("22:00:00", "03:00:00", time);
		
	}
	
	public static boolean checkTime(String start, String stop, String time)	{
		try {
		    
		    Date time1 = new SimpleDateFormat("HH:mm:ss").parse(start);
		    Calendar calendar1 = Calendar.getInstance();
		    calendar1.setTime(time1);

		    Date time2 = new SimpleDateFormat("HH:mm:ss").parse(stop);
		    Calendar calendar2 = Calendar.getInstance();
		    calendar2.setTime(time2);
		    

		    Date d = new SimpleDateFormat("HH:mm:ss").parse(time);
		    Calendar calendar3 = Calendar.getInstance();
		    calendar3.setTime(d);
		    

		    Date x = calendar3.getTime();
		    if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
		        //checkes whether the current time is between 14:49:00 and 20:11:13.
		        return true;
		    }
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return false;
	}
	
	public static double middlePoint(String v1, String v2) {
		double middlePoint = Double.valueOf(v1)+Double.valueOf(v2);
		return middlePoint/2.0;
	}
	
	
	
public static void main(String[] args)	{
		
		
		Utilities.isFoodTime("37.733795","-122.446747");
		
	}
	
	
	

}