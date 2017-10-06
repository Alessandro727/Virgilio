package postgres;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Checkin;
import model.Venue;

public class CheckinPostgres {
	
	
	public static List<Checkin> RetrieveCheckinsByCheckinID(int checkinID) throws PersistenceException {
		List<Checkin> checkins = null;
		Checkin checkin = null;
		Venue venue = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select date, latitude, longitude, name_fq, category_fq"
					+ " from checkins inner join venues"
					+ " on venue_id = venues.id"
					+ " where checkin_id = " + checkinID
					+ " order by date";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				checkins = new LinkedList<Checkin>();
				venue = new Venue();
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venue.setName_fq(result.getString("name_fq"));
				venue.setCategory_fq(result.getString("category_fq"));				
				checkin = new Checkin();
				checkin.setCheckin_id(checkinID);
				checkin.setDate(result.getString("date"));
				checkin.setVenue(venue);
				checkins.add(checkin);
			}
			while (result.next()) {
				venue = new Venue();
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venue.setName_fq(result.getString("name_fq"));
				venue.setCategory_fq(result.getString("category_fq"));				
				checkin = new Checkin();
				checkin.setCheckin_id(checkinID);
				checkin.setDate(result.getString("date"));
				checkin.setVenue(venue);
				checkins.add(checkin);
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
		return checkins;
	}
	
	
	
	
	public static int getNumCheckinsByUser(int user_id) throws PersistenceException {
		int numCheckins = 0;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select id from checkins where user_id = " + user_id;
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			while (result.next()) {
				numCheckins++;
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
		return numCheckins;
	}
	
		

	public static LinkedList<Venue> getDistinctLatitudeAndLongitude() throws PersistenceException {
		
		LinkedList<Venue> venues = null;
		Venue venue = null;
		Long i = (long) 1;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select distinct latitude, longitude from checkins_filtered";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();
				venue.setId(i);
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));				
				venues.add(venue);
				i++;
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(i);
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));				
				venues.add(venue);
				i++;				
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
	
	
	
	public static void deleteCheckinsWhichVenueHasntResult(List<Venue> venues) throws PersistenceException {
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String delete = "delete from checkins_filtered where latitude = ? and longitude = ?";
			for (Venue v: venues) {
				statement = connection.prepareStatement(delete);
				statement.setBigDecimal(1, new BigDecimal(v.getLatitude()));
				statement.setBigDecimal(2, new BigDecimal(v.getLongitude()));
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
	
	
	
	public static Map<Integer, List<Venue>> getCheckinsNumbersByVenueId(List<Venue> venues) throws PersistenceException {
		if (venues == null)
			return null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		Map<Integer, List<Venue>> venuesMap = new TreeMap<Integer, List<Venue>>(Collections.reverseOrder());
		List<Venue> list;		
		Checkin c;
		int size;
		try {
			connection = datasource.getConnection();
			String query = "select id, user_id from checkins where venue_id = ?";
			for (Venue v: venues) {
				statement = connection.prepareStatement(query);
				statement.setLong(1, v.getId());
				result = statement.executeQuery();				
				while (result.next()) {
					c = new Checkin();
					c.setId(result.getInt("id"));
					c.setUser_id(result.getInt("user_id"));
					v.addCheckin(c);
				}
				size = v.getCheckins().size();
				if (v.getCategory_fq().equals("Plaza")) {
					size = size / 10;
				} else {
					if (v.getMacro_category().getId() == 10)
						size = size / 2;
				}
				if (venuesMap.containsKey(size)) {
					list = venuesMap.get(size);
					list.add(v);
					venuesMap.put(size, list);
				}
				else {
					list = new ArrayList<Venue>();
					list.add(v);
					venuesMap.put(size, list);
				}								
			} 
			System.out.println(query);
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
		venuesMap.remove(0);
		return venuesMap;
	}
	
	
	
	public static Venue getCheckinsNumbersByVenueId(Venue venue) throws PersistenceException {
		if (venue == null)
			return null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		Checkin c;
		try {
			connection = datasource.getConnection();
			String query = "select id, user_id from checkins where venue_id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, venue.getId());
			result = statement.executeQuery();				
			while (result.next()) {
				c = new Checkin();
				c.setId(result.getInt("id"));
				c.setUser_id(result.getInt("user_id"));
				venue.addCheckin(c);
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
		return venue;
	}
	
	
	
	public static List<Venue> getCheckinsWithNullVenueId() throws PersistenceException {
		List<Venue> venues = null;
		Venue venue = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select id, latitude, longitude from checkins where venue_id is null limit 500";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();
				venue.setId(result.getLong("id"));	//id del checkin
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
				venues.add(venue);
			}
			while (result.next()) {
				venue = new Venue();
				venue.setId(result.getLong("id"));
				venue.setLatitude(result.getString("latitude"));
				venue.setLongitude(result.getString("longitude"));
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

		
	

	public static void updateCheckins(LinkedList<Venue> venues) throws PersistenceException {
		
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String update = "update checkins set venue_id = ? where latitude = ? and longitude = ?";
			for (Venue v: venues) {
				statement = connection.prepareStatement(update);
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
	
	
	
	// elimina i checkins che hanno meno di 3 checkins giornalieri
	public static void deleteEntryFromCheckinsMinor3() throws PersistenceException, ParseException {
		Date date;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar;
		String[] dates = new String[]{	"2011-01-01", "2011-01-02", "2011-01-03", "2011-01-04",
		                            	"2011-01-05", "2011-01-06", "2011-01-07", "2011-01-08",
		                            	"2011-01-09", "2011-01-10", "2011-01-11", "2011-01-12",
		                            	"2011-01-13", "2011-01-14", "2011-01-15", "2011-01-16",
		                            	"2011-01-17", "2011-01-18", "2011-01-19", "2011-01-20",
			                            "2011-01-21", "2011-01-22", "2011-01-23", "2011-01-24",
			                            "2011-01-25", "2011-01-26", "2011-01-27", "2011-01-28",
			                            "2011-01-29", "2011-01-30", "2011-01-31",
			                            "2011-02-01", "2011-02-02", "2011-02-03", "2011-02-04",
			                            "2011-02-05", "2011-02-06", "2011-02-07", "2011-02-08",
			                            "2011-02-09", "2011-02-10", "2011-02-11", "2011-02-12",
			                            "2011-02-13", "2011-02-14", "2011-02-15", "2011-02-16",
			                            "2011-02-17", "2011-02-18", "2011-02-19", "2011-02-20",
			                            "2011-02-21", "2011-02-22", "2011-02-23", "2011-02-24",
			                            "2011-02-28",
			                            "2011-03-01", "2011-03-02", "2011-03-03", "2011-03-04",
			                            "2011-03-05", "2011-03-06", "2011-03-07", "2011-03-08",
			                            "2011-03-09", "2011-03-10", "2011-03-11", "2011-03-12",
			                            "2011-03-13", "2011-03-14", "2011-03-15", "2011-03-16",
			                            "2011-03-17", "2011-03-18", "2011-03-19", "2011-03-20",
			                            "2011-03-21", "2011-03-22", "2011-03-23", "2011-03-24",
			                            "2011-03-25", "2011-03-26", "2011-03-27", "2011-03-28",
			                            "2011-03-29", "2011-03-30", "2011-03-31",
			                            "2011-04-01", "2011-04-02", "2011-04-03", "2011-04-04",
			                            "2011-04-05", "2011-04-06", "2011-04-07", "2011-04-08",
			                            "2011-04-09", "2011-04-10", "2011-04-11", "2011-04-12",
			                            "2011-04-13", "2011-04-14", "2011-04-15", "2011-04-16",
			                            "2011-04-17", "2011-04-18", "2011-04-19", "2011-04-20",
			                            "2011-04-21", "2011-04-22", "2011-04-23", "2011-04-24",
			                            "2011-04-25", "2011-04-26", "2011-04-27", "2011-04-28",
			                            "2011-04-29", "2011-04-30",
			                            "2011-05-01", "2011-05-02", "2011-05-03", "2011-05-04",
			                            "2011-05-05", "2011-05-06", "2011-05-07", "2011-05-08",
			                            "2011-05-09", "2011-05-10", "2011-05-11", "2011-05-12",
			                            "2011-05-13", "2011-05-14", "2011-05-15", "2011-05-16",
			                            "2011-05-17", "2011-05-18", "2011-05-19", "2011-05-20",
			                            "2011-05-21", "2011-05-22", "2011-05-23", "2011-05-24",
			                            "2011-05-25", "2011-05-26", "2011-05-27", "2011-05-28",
			                            "2011-05-29", "2011-05-30", "2011-05-31",
			                            "2011-06-01", "2011-06-02", "2011-06-03", "2011-06-04",
			                            "2011-06-05", "2011-06-06", "2011-06-07", "2011-06-08",
			                            "2011-06-09", "2011-06-10", "2011-06-11", "2011-06-12",
			                            "2011-06-13", "2011-06-14", "2011-06-15", "2011-06-16",
			                            "2011-06-17", "2011-06-18", "2011-06-19", "2011-06-20",
			                            "2011-06-21", "2011-06-22", "2011-06-23", "2011-06-24",
			                            "2011-06-25", "2011-06-26", "2011-06-27", "2011-06-28",
			                            "2011-06-29", "2011-06-30",
			                            "2011-07-01", "2011-07-02", "2011-07-03", "2011-07-04",
			                            "2011-07-05", "2011-07-06", "2011-07-07", "2011-07-08",
			                            "2011-07-09", "2011-07-10", "2011-07-11", "2011-07-12",
			                            "2011-07-13", "2011-07-14", "2011-07-15", "2011-07-16",
			                            "2011-07-17", "2011-07-18", "2011-07-19", "2011-07-20",
			                            "2011-07-21", "2011-07-22", "2011-07-23", "2011-07-24",
			                            "2011-07-25", "2011-07-26", "2011-07-27", "2011-07-28",
			                            "2011-07-29", "2011-07-30", "2011-07-31" };
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;		
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			for (String s: dates) {
				calendar = new GregorianCalendar();
				System.out.print("Inizio: " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " --  s=" + s);
				String query =	"select user_id " +
								"from " +
									"(select user_id, count(date) " +
									"from " +
										"(select user_id, date(date) " +
										"from checkins_filtered " +
										"where date(date) = ? " +
										"order by user_id) as A " +
									"group by user_id) as B " +
								"where count < 3";
				statement = connection.prepareStatement(query);
				date = df.parse(s);
				statement.setTimestamp(1, new java.sql.Timestamp(date.getTime()));		
				result = statement.executeQuery();
				
				String delete = "delete from checkins_filtered where user_id = ? and date(date) = ?";
				while (result.next()) {
					statement = connection.prepareStatement(delete);
					statement.setInt(1, result.getInt("user_id"));
					statement.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
					statement.executeUpdate();								
				}
				calendar = new GregorianCalendar();
				System.out.print("  -- Fine: " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
				System.out.println();
				System.out.println();
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

	
	
	// inserisce il checkin_id ai checkins dello stesso giorno e utente
	public static void addCheckinId(int userIdFrom, int userIdTo, int i) throws PersistenceException, ParseException {
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;		
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query =	"select distinct user_id, date(date)"
							+ "	from checkins"
							+ " where user_id >= " + userIdFrom + " and user_id <= " + userIdTo
							+ " group by user_id, date";
			statement = connection.prepareStatement(query);			
			result = statement.executeQuery();
			
			String update = "update checkins set checkin_id = ? where user_id = ? and date(date) = ?";
			while (result.next()) {
				statement = connection.prepareStatement(update);
				statement.setInt(1, i);
				statement.setInt(2, result.getInt("user_id"));
				statement.setTimestamp(3, result.getTimestamp("date"));
				statement.executeUpdate();
				i++;
			}
			System.out.println("i finale = " + (i-1));
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
	
	
	
	
	public static void updateVenueId2Checkins(List<Venue> venues) throws PersistenceException {
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;		
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			for (Venue v: venues) {
				String query = "select id from venues_filtered where latitude = ? and longitude = ?";
				statement = connection.prepareStatement(query);
				statement.setBigDecimal(1, new BigDecimal(v.getLatitude()));		
				statement.setBigDecimal(2, new BigDecimal(v.getLongitude()));
				result = statement.executeQuery();
				
				String update = "update checkins set venue_id = ? where venue_id = ?";
				while (result.next()) {
					statement = connection.prepareStatement(update);
					statement.setLong(1, v.getId());
					statement.setInt(2, result.getInt("id"));
					statement.executeUpdate();								
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
}