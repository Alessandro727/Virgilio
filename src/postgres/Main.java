package postgres;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Random;
//import java.util.TimeZone;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import socialAndServices.Foursquare;
//import socialAndServices.Google;
//import util.GeoLocation;
//import fi.foyt.foursquare.api.FoursquareApiException;
//import logic.LatLngSquare;
import model.MacroCategory;
import model.User;
import model.Venue;



public class Main {

	public static void main(String[] args) {

		try {
			User user = UserPostgres.RetrieveUserByUsernameAndPassword("dario", "dario");
			System.out.println(cosenoSim(user));
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		/*Google google = new Google();
		Venue start = google.getCoordinatesFromAddress("Place Saint-Michel, paris");		
		Venue end = google.getCoordinatesFromAddress("Arc de Triomphe, paris");
		List<Venue> list = new ArrayList<Venue>();
		list.add(start);
		list.add(end);
		LatLngSquare llSquare = new LatLngSquare(list);
		System.out.println(" latitude >= " + llSquare.getMinLat() + " and latitude <= " + llSquare.getMaxLat()
						 + " longitude >= " + llSquare.getMinLng() + " and longitude <= " + llSquare.getMaxLng());
		try {
			List<Venue> venues = VenuePostgres.retrieveVenuesBySquareLimits(llSquare);
			System.out.println(venues.size());
			VenuePostgres.updateFoursquareId(venues);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}*/


		/*try {
			List<Venue> venues = VenuePostgres.RetrieveVenuesWIthNullFoursquareId(33001, 35000);
			System.out.println(venues.size());
			VenuePostgres.updateFoursquareId(venues);

		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		//spegniPC();

	}



	public static int cosenoSim(User user) throws PersistenceException {
		List<User> users = UserPostgres.getAllUsers();
		int id = -1;
		int numCheckins = 0;

		double AB;		// A�B
		double A = 0;	// A
		double B;		// B^2
		double sim = 0;
		double simTemp;

		for (int i=1; i<user.getWeigths().length; i++) {
			A += Math.pow(user.getWeigth(i), 2);
		}

		for (User u: users) {
			AB = 0;
			B = 0;			
			for (int i=1; i<user.getWeigths().length; i++) {
				AB += (user.getWeigth(i)) * (u.getWeigth(i));
				B += Math.pow(u.getWeigth(i), 2);
			}
			simTemp = ( AB / Math.sqrt(A*B) );
			if (simTemp > sim) {
				sim = simTemp;
				id = u.getId();
				numCheckins = CheckinPostgres.getNumCheckinsByUser(u.getId());				
			} else {
				if (simTemp == sim) {
					int ck = CheckinPostgres.getNumCheckinsByUser(u.getId());
					if (ck > numCheckins) {						
						id = u.getId();
						numCheckins = ck;
					}
				}
			}
		}

		return id;
	}







	public static void creaNomiRandom(List<Integer> ids) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			String path = "http://www.behindthename.com/api/random.php?number=6&gender=both&usage=ita&usage=cat&usage=dut&usage&eng&usage=fre&usage=ger&usage=por&usage=spa&usage=swe&key=da807156";
			Document document;
			Element response, namesElement;
			NodeList names;
			List<User> users = new ArrayList<User>();
			User u = null;

			for (int j=0; j<3; j++) {
				document = builder.parse(new URL(path).openStream());
				response = (Element)document.getElementsByTagName("response").item(0);
				namesElement = (Element)response.getElementsByTagName("names").item(0);
				names = namesElement.getElementsByTagName("name");

				for (int i=0; i<6; i+=2) {
					u = new User();
					u.setId(ids.remove(0));
					u.setUsername(names.item(i).getFirstChild().getNodeValue());
					u.setPassword(names.item(i+1).getFirstChild().getNodeValue());
					users.add(u);                	
				}

				Thread.sleep(4000); // 4 secondi
			}

			UserPostgres.updateUsernameAndPassword(users);        	

		} catch (SAXException sxe) {
			Exception  x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}







	public static void stampaArray(double[] pesi) {
		System.out.print("user " + pesi[0] + "\t: ");
		for (int i=1; i<pesi.length; i++)
			System.out.print(pesi[i] + " - ");
		System.out.println();
	}



	public static void aggiornaPesiUtenti(int from, int to) {
		List<Integer> userId;
		List<MacroCategory> macroCategories;
		List<double[]> weights = new ArrayList<double[]>();
		double[] pesi = new double[]{0,0,0,0,0,0,0,0,0,0,0};		
		double peso;
		int total = 0;

		try {
			userId = UserPostgres.RetrieveUserIdFromTo(from, to);
			for (Integer id: userId) {
				pesi = new double[]{0,0,0,0,0,0,0,0,0,0,0};
				pesi[0] = id;

				macroCategories = UserPostgres.RetrieveMacroCategoryByUser(id);
				total = macroCategories.size();

				for(MacroCategory mc: macroCategories) {
					peso = pesi[mc.getId()] + 1;
					pesi[mc.getId()] = peso;
				}

				for(int i=1; i<11; i++) {			
					pesi[i] = pesi[i]/total;
					if (pesi[i] < 0.8) {
						pesi[i] = Math.floor((pesi[i]+0.15)*100)/100;
					} else {
						if (pesi[i] < 0.9)
							pesi[i] = Math.floor((pesi[i]+0.1)*100)/100;
						else							
							pesi[i] = Math.floor((pesi[i])*100)/100;
					}			
				}
				weights.add(pesi);
				//stampaArray(pesi);
			}

			UserPostgres.aggiornaPesi(weights);

		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public static void aggiornaFoursquareId() {

		// 186.936
		List<Venue> venues;
		try {		

			venues = VenuePostgres.RetrieveVenuesWIthNullFoursquareId(4501, 5000);
			System.out.println(venues.size());
			System.out.print(" --- fino a 5000");
			VenuePostgres.updateFoursquareId(venues);

		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}






	public static Map<Integer, List<String>> foursquareCategories() {
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();

		try {
			URL url = new URL("https://api.foursquare.com/v2/venues/categories?locale=en&oauth_token=4SXRSPRRDEUFUOK1SJTKBW0JIC5OX21EOV4JK20MIORNIMTS&v=20140325");
			URLConnection conn = url.openConnection();                                                                    
			conn.connect();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			StringBuffer sb = new StringBuffer();

			for (int i=0; i!=-1; i=isr.read()) {   
				sb.append((char)i);
			}
			String jsonString = sb.toString().trim();

			JSONObject jsonObject = new JSONObject(jsonString);

			JSONObject response = jsonObject.getJSONObject("response");                             
			JSONArray macroCategories = response.getJSONArray("categories");
			JSONArray subCategories;
			JSONArray subSubCategories;
			//String cat;
			String subCat;
			String subSubCat;

			map.put(1, new ArrayList<String>());
			map.put(2, new ArrayList<String>());
			map.put(3, new ArrayList<String>());
			map.put(4, new ArrayList<String>());
			map.put(5, new ArrayList<String>());
			map.put(6, new ArrayList<String>());
			map.put(7, new ArrayList<String>());
			map.put(8, new ArrayList<String>());
			map.put(9, new ArrayList<String>());
			map.put(10, new ArrayList<String>());
			for (int i=0; i<macroCategories.length(); i++) {
				//cat = macroCategories.getJSONObject(i).get("name").toString();
				subCategories = macroCategories.getJSONObject(i).getJSONArray("categories");
				for (int j=0; j<subCategories.length(); j++) {
					subCat = subCategories.getJSONObject(j).get("name").toString();
					map.get(i+1).add(subCat);
					subSubCategories = subCategories.getJSONObject(j).getJSONArray("categories");
					for (int k=0; k<subSubCategories.length(); k++) {
						subSubCat = subSubCategories.getJSONObject(k).get("name").toString();
						map.get(i+1).add(subSubCat);                    	
					}
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map;
	}





	public static void spegniPC() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec("C:\\WINDOWS\\system32\\cmd.exe");
			OutputStream os = process.getOutputStream();
			os.write("shutdown -s -f -t 0\n\r".getBytes());
			os.close();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}