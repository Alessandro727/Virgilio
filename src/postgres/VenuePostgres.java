package postgres;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import logic.LatLngSquare;
import model.MacroCategory;
import model.User;
import model.Venue;
import util.Utilities;

public class VenuePostgres {



	public static List<Venue> RetrieveVenuesByDistance(int radius, double lat, double lng) throws PersistenceException {
		List<Venue> venues = null;
		Venue venue = null;
		double difference = radius * 0.01;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select *"
					+ " from venues"
					+ " where latitude >= " + (lat-difference) + " and latitude <= " + (lat+difference)
					+ " and longitude >= " + (lng-difference) + " and longitude <= " + (lng+difference);
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();
				venue.setId(result.getLong("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venue.setName_fq(result.getString("name_fq"));
				venue.setCategory_fq(result.getString("category_fq"));				
				venues.add(venue);				
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(result.getLong("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venue.setName_fq(result.getString("name_fq"));
				venue.setCategory_fq(result.getString("category_fq"));				
				venues.add(venue);
			} 
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return venues;
	}






	public static void persistVenues(LinkedList<Venue> venues) throws PersistenceException {

		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String insert = "insert into venues_original (id, latitude_or, longitude_or) values (?, ?, ?)";
			for (Venue v: venues) {
				statement = connection.prepareStatement(insert);
				statement.setLong(1, v.getId());
				statement.setBigDecimal(2, new BigDecimal(v.getLatitude()));
				statement.setBigDecimal(3, new BigDecimal(v.getLongitude()));				
				statement.executeUpdate();
			}			
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			throw e;
		} finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}



	public static void deleteVenuesById(List<Venue> venues) {
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;

		String delete = "delete from venues_filtered where id = ?";

		try {
			connection = datasource.getConnection();
			for (Venue v: venues) {
				statement = connection.prepareStatement(delete);
				statement.setLong(1, v.getId());
				statement.executeUpdate();				
			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}



	public static List<Venue> getVenuesFromVenues(int from, int to) throws PersistenceException {
		List<Venue> venues = null;
		Venue venue = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			//String query = "select *"
			//			+ " from venues"
			//			+ " where id >= " + from + " and id <= " + to;
			String query = "select id, latitude, longitude"
					+ " from venues"
					+ " where id >= " + from + " and id <= " + to;
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();
				venue.setId(result.getLong("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				//venue.setName_fq(result.getString("name_fq"));
				//venue.setName_fq(result.getString("category_fq"));
				venues.add(venue);				
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(result.getLong("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				//venue.setName_fq(result.getString("name_fq"));
				//venue.setName_fq(result.getString("category_fq"));
				venues.add(venue);
			} 
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return venues;
	}



	// prende i venues dalla tabella venues il cui id � compreso tra idFrom e idTo
	public static LinkedList<Venue> getVenuesFromVenuesFiltered(int idFrom, int idTo) throws PersistenceException {

		LinkedList<Venue> venues = null;
		Venue venue = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select * from venues_filtered where id >= " + idFrom + " and id <= " + idTo;
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();
				venue.setId(result.getLong("id"));
				venue.setLatitude(result.getString("latitude_or"));
				venue.setLongitude(result.getString("longitude_or"));
				venues.add(venue);
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(result.getLong("id"));
				venue.setLatitude(result.getString("latitude_or"));
				venue.setLongitude(result.getString("longitude_or"));
				venues.add(venue);		
			} 
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return venues;
	}


	/**
	 * 
	 * @param llSquare
	 * @return	tutti i venue all'interno del quadrato
	 * @throws PersistenceException
	 */
	public static List<Venue> retrieveVenuesBySquareLimits(LatLngSquare llSquare) throws PersistenceException {
		List<Venue> venues = null;
		Venue venue = null;
		MacroCategory macroCategory = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select v.id, v.latitude, v.longitude, v.name_fq, v.foursquare_id,"
					+ " c.id, c.category_fq,"
					+ " mc.id, mc.macro_category_fq, mc.mrt"

						+ " from venues v left outer join categories c"
						+ " on v.category_fq_id = c.id"
						+ " left outer join macro_categories mc"
						+ " on c.macro_category_id = mc.id"

						+ " where v.latitude >= " + llSquare.getMinLat() + " and v.latitude <= " + llSquare.getMaxLat() 
						+ " and v.longitude >= " + llSquare.getMinLng() + " and v.longitude <= " + llSquare.getMaxLng();
			System.out.println(query);
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();				 
				venue.setId(result.getLong("v.id"));
				venue.setLatitude(result.getString("v.latitude"));
				venue.setLongitude(result.getString("v.longitude"));
				venue.setName_fq(result.getString("v.name_fq"));
				venue.setCategory_fq(result.getString("c.category_fq"));
				venue.setFoursquare_id(result.getString("foursquare_id"));
				macroCategory = new MacroCategory();
				macroCategory.setId(result.getInt("mc.id"));
				macroCategory.setMacro_category_fq(result.getString("mc.macro_category_fq"));
				macroCategory.setMrt(result.getInt("mc.mrt"));
				venue.setMacro_category(macroCategory);
				venues.add(venue);				
			}
			while (result.next()) {
				venue = new Venue();				 
				venue.setId(result.getLong("v.id"));
				venue.setLatitude(result.getString("v.latitude"));
				venue.setLongitude(result.getString("v.longitude"));
				venue.setName_fq(result.getString("v.name_fq"));
				venue.setCategory_fq(result.getString("c.category_fq"));
				venue.setFoursquare_id(result.getString("foursquare_id"));
				macroCategory = new MacroCategory();
				macroCategory.setId(result.getInt("mc.id"));
				macroCategory.setMacro_category_fq(result.getString("mc.macro_category_fq"));
				macroCategory.setMrt(result.getInt("mc.mrt"));
				venue.setMacro_category(macroCategory);
				venues.add(venue);	
			} 
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return venues;
	}


	/**
	 * 
	 * @param llSquare
	 * @param cats (es.: mc.id = 5 and..., mc.id != 5 and...
	 * @return	tutti i venue all'interno del quadrato che appartangono alle categorie incluse o escluse nella stringa cats 
	 * @throws PersistenceException
	 */
	public static List<Venue> retrieveVenuesBySquareLimitsAndCategories(LatLngSquare llSquare, String cats) throws PersistenceException {
		List<Venue> venues = null;
		Venue venue = null;
		MacroCategory macroCategory = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select v.id, v.latitude, v.longitude, v.name_fq, v.foursquare_id,"
					+ " c.id, c.category_fq,"
					+ " mc.id, mc.macro_category_fq, mc.mrt"

						+ " from venues v left outer join categories c"
						+ " on v.category_fq_id = c.id"
						+ " left outer join macro_categories mc"
						+ " on c.macro_category_id = mc.id"

						+ " where " + cats

						+ " and v.latitude >= " + llSquare.getMinLat() + " and v.latitude <= " + llSquare.getMaxLat() 
						+ " and v.longitude >= " + llSquare.getMinLng() + " and v.longitude <= " + llSquare.getMaxLng();
			System.out.println(query);
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();				 
				venue.setId(result.getLong("v.id"));
				venue.setLatitude(result.getString("v.latitude"));
				venue.setLongitude(result.getString("v.longitude"));
				venue.setName_fq(result.getString("v.name_fq"));
				venue.setCategory_fq(result.getString("c.category_fq"));
				venue.setFoursquare_id(result.getString("foursquare_id"));
				macroCategory = new MacroCategory();
				macroCategory.setId(result.getInt("mc.id"));
				macroCategory.setMacro_category_fq(result.getString("mc.macro_category_fq"));
				macroCategory.setMrt(result.getInt("mc.mrt"));
				venue.setMacro_category(macroCategory);
				venues.add(venue);				
			}
			while (result.next()) {
				venue = new Venue();				 
				venue.setId(result.getLong("v.id"));
				venue.setLatitude(result.getString("v.latitude"));
				venue.setLongitude(result.getString("v.longitude"));
				venue.setName_fq(result.getString("v.name_fq"));
				venue.setCategory_fq(result.getString("c.category_fq"));
				venue.setFoursquare_id(result.getString("foursquare_id"));
				macroCategory = new MacroCategory();
				macroCategory.setId(result.getInt("mc.id"));
				macroCategory.setMacro_category_fq(result.getString("mc.macro_category_fq"));
				macroCategory.setMrt(result.getInt("mc.mrt"));
				venue.setMacro_category(macroCategory);
				venues.add(venue);	
			} 
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return venues;
	}


	public static List<Venue> retriveOnlyNewVenue(List<Venue> venues, User user) throws PersistenceException	{

		List<Long> venueIdVisited = new LinkedList<>();
		long userId = UserPostgres.retriveUserIdByUsername(user.getUsername());
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "SELECT venue_id FROM checkins WHERE user_id = "+userId;
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			while (result.next()) {

				venueIdVisited.add(result.getLong("venue_id"));
			} 

		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}


		for (Iterator<Venue> it = venues.iterator(); it.hasNext();) {
			Venue venue = it.next();
			if (venueIdVisited.contains(venue.getId()))	{
				it.remove();
			}
		}
		
		
		
		return venues;

	}


	public static  List<Venue>	retriveAllResidenceVenues(List<Venue> venues, double lat, double lon, double radius) throws PersistenceException	{

		double  lat1 = lat - radius,
				lat2 = lat + radius,
				lon1 = lon - radius,
				lon2 = lon + radius;
		
		Map<Venue, Integer> venueMap = new HashMap<>();
		
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		for (Venue venue : venues) {


			try {
				connection = datasource.getConnection();
				String query = "SELECT venue_id FROM checkins, users WHERE checkins.user_id = users.id AND users.residenceLat > "
						+lat1+" AND users.residenceLat <= "+lat2+" AND users.residenceLong > "+lon1+" AND users.residenceLong <= "+lon2+" AND checkins.venue_id = "+venue.getId();
				statement = connection.prepareStatement(query);
				result = statement.executeQuery();
				if (result.next()) {
					Integer count = venueMap.get(venue);
					if (count == null) {
					    venueMap.put(venue, 1);
					}
					else {
					    venueMap.put(venue, count + 1);
					}
				}
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			} finally {
				try {
					if (result != null)
						result.close();
					if (statement != null) 
						statement.close();
					if (connection!= null)
						connection.close();
				} catch (SQLException e) {
					throw new PersistenceException(e.getMessage());
				}
			}
		}
		
		System.out.println("Dimesione MAPPA luoghi più visitati da utenti esperti = "+venueMap.size());
		
		
		venueMap = Utilities.sortByValue(venueMap);

		return new ArrayList<>(venueMap.keySet());

	}
	

	public static List<Venue> venuesVisitedFromSimilarUsers(List<Venue> venues, List<Long> similarUsers) throws PersistenceException	{
		
		Map<Venue, Integer> venuesMap = new HashMap<>();
		
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		
		for (Venue venue : venues) {
			
			try {
				connection = datasource.getConnection();
				String query = "SELECT venue_id, COUNT(user_id) as userNumber FROM checkins, users WHERE checkins.user_id = users.id AND venue_id = "+venue.getId()
				+" AND ( ";
				for (Long long1 : similarUsers) {
					query = query+"user_id = "+long1+" OR ";
				}
				query = query+"false) GROUP BY venue_id";
				statement = connection.prepareStatement(query);
				result = statement.executeQuery();
				if (result.next()) {
					
					Integer userNumber = result.getInt("userNumber");
					venuesMap.put(venue, userNumber);
				}
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			} finally {
				try {
					if (result != null)
						result.close();
					if (statement != null) 
						statement.close();
					if (connection!= null)
						connection.close();
				} catch (SQLException e) {
					throw new PersistenceException(e.getMessage());
				}
			}
			
		}
		
		System.out.println("Dimesione MAPPA luoghi più visitati da utenti più simili = "+venuesMap.size());
		
		
		venuesMap = Utilities.sortByValue(venuesMap);
		
		
		return new ArrayList<>(venuesMap.keySet());
		
		
	}
	
	public static void main(String args[])	{
		
		List<Long> similarUsers =  new ArrayList<>();
		
		similarUsers.add((long) 123);
		similarUsers.add((long) 1223);
		
		String query = "SELECT venue_id, COUNT(user_id) as userNumber FROM checkins, users WHERE checkins.user_id = users.id AND venue_id = 3440"
		+" AND ( ";
		for (Long long1 : similarUsers) {
			query = query+"user_id = "+long1+" OR ";
		}
		query = query+"false) GROUP BY venue_id";
		
		System.out.println(query);
		
	}


}