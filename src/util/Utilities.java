package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import logic.router.Node;
import model.Venue;
import net.aksingh.owmjapis.AbstractWeather.Weather;
import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

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
				if (sCurrentLine.contains("FQ_CLIENT_ID"))	{
					clientID =  sCurrentLine.split("FQ_CLIENT_ID=")[1];
				}
				if (sCurrentLine.contains("FQ_CLIENT_SECRET"))	{
					clientSecret =  sCurrentLine.split("FQ_CLIENT_SECRET=")[1];
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				if (sCurrentLine.contains("OWM_KEY"))	{
					owmKey =  sCurrentLine.split("OWM_KEY=")[1];
				}
				

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		OpenWeatherMap opw = new OpenWeatherMap(owmKey);
		int result = 0;
		int limit = 2;
		if (availableTime >= 200)
			limit = 3;
		try {
			CurrentWeather fwd = opw.currentWeatherByCoordinates(Float.valueOf(v.getLatitude()), Float.valueOf(v.getLongitude()));
			Weather w;
			for (int i=1; i<=limit; i++) {
				w = fwd.getWeatherInstance(i);
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

}