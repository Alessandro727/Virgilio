package logic.router;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.register.Register.Int;

import model.MacroCategory;
import model.Venue;
import util.JsonReader;
import util.Utilities;

public class FoursquareManagerForPlace {

	public static List<Venue> searchVenues(String ll, List<String> categories) throws JSONException, IOException {

		String categoryIds = createCategoryIdFoursquare(categories);

		List<Venue> venues = new ArrayList<>();

		String CLIENT_ID = null;
		String CLIENT_SECRET = null;
		Properties prop = new Properties();
		InputStream input = null;
		try {

			prop.load(Utilities.class.getClassLoader().getResourceAsStream("config.properties"));
			// get the property value and print it out

			CLIENT_ID = prop.getProperty("FQ_CLIENT_ID");
			CLIENT_SECRET = prop.getProperty("FQ_CLIENT_SECRET");


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

		for (int j=0; j<categoryIds.split(",").length;j++) {




			JSONObject json = JsonReader.readJsonFromUrl("https://api.foursquare.com/v2/venues/search?ll="+ll+"&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&v=20171203&categoryId="+categoryIds.split(",")[j]);

			JSONArray array = json.getJSONObject("response").getJSONArray("venues");



			for(int i=0; i<array.length(); i++)	{
				Venue obj = new Venue();
				obj.setLatitude(String.valueOf(array.getJSONObject(i).getJSONObject("location").getDouble("lat")));
				obj.setLongitude(String.valueOf(array.getJSONObject(i).getJSONObject("location").getDouble("lng")));
				String idVenue = array.getJSONObject(i).getString("id");
				Long idHash = UUID.nameUUIDFromBytes(idVenue.getBytes()).getMostSignificantBits();

				obj.setId(idHash);
				obj.setName_fq(array.getJSONObject(i).getString("name"));
				MacroCategory mCategory = new MacroCategory();
				String categoryName = array.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name");
				mCategory.setMacro_category_fq(categoryName);


				mCategory.setId(Integer.parseInt(categories.get(j)));
				mCategory.addMeanResidenceTime(categories.get(j));

				obj.setCategory_fq(categoryName);
				obj.setMacro_category(mCategory);

				System.out.println(mCategory.getId());

				venues.add(obj);
			}
		
		}
		//		System.out.println(json.toString());

		return venues;

	}



	private static String createCategoryIdFoursquare(List<String> categories) {
		String category = "";
		String primo = categories.get(0);
		if(primo.equals("1"))
			category = category+"4bf58dd8d48988d181941735";
		if(primo.equals("2"))
			category = category+"4bf58dd8d48988d131941735";
		if(primo.equals("3"))
			category = category+"4bf58dd8d48988d12d941735";
		if(primo.equals("4"))
			category = category+"4d4b7104d754a06370d81259";
		if(primo.equals("5"))
			category = category+"4d4b7105d754a06377d81259";
		if(primo.equals("6"))
			category = category+"4d4b7105d754a06374d81259";
		if(primo.equals("7"))
			category = category+"56aa371be4b08b9a8d573554";
		if(primo.equals("8"))
			category = category+"4d4b7105d754a06376d81259";
		if(primo.equals("9"))
			category = category+"4d4b7105d754a06378d81259";
		if(primo.equals("10"))
			category = category+"4f4528bc4b90abdf24c9de85";

		String removed = categories.remove(0);

		if(categories.contains("1"))
			category = category+",4bf58dd8d48988d181941735";
		if(categories.contains("2"))
			category = category+",4bf58dd8d48988d131941735";
		if(categories.contains("3"))
			category = category+",4bf58dd8d48988d12d941735";
		if(categories.contains("4"))
			category = category+",4d4b7104d754a06370d81259";
		if(categories.contains("5"))
			category = category+",4d4b7105d754a06377d81259";
		if(categories.contains("6"))
			category = category+",4d4b7105d754a06374d81259";
		if(categories.contains("7"))
			category = category+",56aa371be4b08b9a8d573554";
		if(categories.contains("8"))
			category = category+",4d4b7105d754a06376d81259";
		if(categories.contains("9"))
			category = category+",4d4b7105d754a06378d81259";
		if(categories.contains("10"))
			category = category+",4f4528bc4b90abdf24c9de85";
		
		categories.add(0, removed);
		return category;
	}

	public static void main(String[] args) throws JSONException, IOException	{

		List<String> categories = new ArrayList<>();
		categories.add("1");
		categories.add("2");

		List<Venue> venues = searchVenues("41.8919300,12.5113300", categories);
		
		System.out.println(venues.size());

	}


}
