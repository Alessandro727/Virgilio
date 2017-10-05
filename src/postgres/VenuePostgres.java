package postgres;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import socialAndServices.Foursquare;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.entities.CompactVenue;
import logic.LatLngSquare;
import model.MacroCategory;
import model.Venue;

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
				venue.setId(result.getInt("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venue.setName_fq(result.getString("name_fq"));
				venue.setCategory_fq(result.getString("category_fq"));				
				venues.add(venue);				
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(result.getInt("id"));
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
	
	
	
	public static List<Venue> RetrieveVenuesWIthNullFoursquareId(int from, int to) throws PersistenceException {
		List<Venue> venues = null;
		Venue venue = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select * from venues where foursquare_id is null and id >= " + from + " and id <= " + to;
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();
				venue.setId(result.getInt("id"));				
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venue.setName_fq(result.getString("name_fq"));
				venues.add(venue);				
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(result.getInt("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venue.setName_fq(result.getString("name_fq"));
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
				statement.setInt(1, v.getId());
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
				statement.setInt(1, v.getId());
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
				venue.setId(result.getInt("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				//venue.setName_fq(result.getString("name_fq"));
				//venue.setName_fq(result.getString("category_fq"));
				venues.add(venue);				
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(result.getInt("id"));
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
			venue.setId(result.getInt("id"));
			venue.setLatitude(result.getString("latitude_or"));
			venue.setLongitude(result.getString("longitude_or"));
			venues.add(venue);
		}
		while (result.next()) {
			venue = new Venue();
			venue.setId(result.getInt("id"));
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
				venue.setId(result.getInt("v.id"));
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
				venue.setId(result.getInt("v.id"));
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
				venue.setId(result.getInt("v.id"));
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
				venue.setId(result.getInt("v.id"));
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
	
	
	
	

	public static void updateFoursquareId(List<Venue> venues) throws PersistenceException {
		
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		CompactVenue cv;
		try {
			connection = datasource.getConnection();
			String update = "update venues set foursquare_id = ? where id = ?";
			for (Venue v: venues) {
				if (v.getFoursquare_id() != null)
					continue;
				
				cv = Foursquare.searchSingleVenueMatch(v);
				if (cv != null) {
					if (cv.getName().equals(v.getName_fq())) {
						statement = connection.prepareStatement(update);
						statement.setString(1, cv.getId());
						statement.setInt(2, v.getId());
						statement.executeUpdate();												
					}
					else System.out.println(v.getId() + " - non uguale");
				}								
			}			
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			throw e;
		} catch (FoursquareApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	
	


}