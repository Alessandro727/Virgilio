package postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Checkin;
import model.MacroCategory;
import model.User;

public class UserPostgres {



	public static List<Checkin> RetrieveCheckinsByUser(int userID) throws PersistenceException {
		List<Checkin> checkins = null;
		Checkin checkin = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select date, checkin_id, count(*) as count"
					+ " from"
					+ " 	(select date(date) as date, checkin_id"
					+ " 	from checkins"
					+ "		where user_id = " + userID + ") as A"
					+ " group by date, checkin_id order by date";			
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				checkins = new LinkedList<Checkin>();
				checkin = new Checkin();
				checkin.setCheckin_id(result.getInt("checkin_id"));
				checkin.setDateAsString(result.getString("date"));
				checkin.setLocation_id(result.getInt("count"));	//escamotage
				checkins.add(checkin);
			}
			while (result.next()) {
				checkin = new Checkin();
				checkin.setCheckin_id(result.getInt("checkin_id"));
				checkin.setDateAsString(result.getString("date"));
				checkin.setLocation_id(result.getInt("count"));	//escamotage
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



	public static User RetrieveUserByUsernameAndPassword(String username, String password) throws PersistenceException {
		User user = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select * from users where username = '" + username.replace("'", "''") + "' and password = '" + password.replace("'", "''") + "'";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				user = new User();
				user.setId(result.getInt("id"));
				user.setUsername(username);
				user.setPassword(password);
				user.setWeight(1, result.getDouble("1"));
				user.setWeight(2, result.getDouble("2"));
				user.setWeight(3, result.getDouble("3"));
				user.setWeight(4, result.getDouble("4"));
				user.setWeight(5, result.getDouble("5"));
				user.setWeight(6, result.getDouble("6"));
				user.setWeight(7, result.getDouble("7"));
				user.setWeight(8, result.getDouble("8"));
				user.setWeight(9, result.getDouble("9"));
				user.setWeight(10, result.getDouble("10"));
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
		return user;
	}

	public static boolean RetriveUsername(String username) throws PersistenceException	{
		boolean usernameExist = false;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select * from users where username = '" + username.replace("'", "''") + "'";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			while (result.next())	{
				usernameExist = true;
				return usernameExist;
			}
		}
		catch (SQLException e) {
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
		return usernameExist;

	}



	public static User RetrieveUserById(int id) throws PersistenceException {
		User user = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select * from users where id = " + id;
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				user = new User();
				user.setId(id);
				user.setUsername(result.getString("username"));
				user.setPassword(result.getString("password"));
				user.setWeight(1, result.getDouble("1"));
				user.setWeight(2, result.getDouble("2"));
				user.setWeight(3, result.getDouble("3"));
				user.setWeight(4, result.getDouble("4"));
				user.setWeight(5, result.getDouble("5"));
				user.setWeight(6, result.getDouble("6"));
				user.setWeight(7, result.getDouble("7"));
				user.setWeight(8, result.getDouble("8"));
				user.setWeight(9, result.getDouble("9"));
				user.setWeight(10, result.getDouble("10"));
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
		return user;
	}



	public static void RetrieveFriends(User user) throws PersistenceException {
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select distinct(friend_id) from friendship where user_id = " + user.getId();
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			while (result.next())
				user.addFriend(result.getInt("friend_id"));
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


	public static List<Integer> RetrieveUserIdFromTo(int from, int to) throws PersistenceException {
		List<Integer> list = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select id from users where id >= " + from + " and id <= " + to + " order by id";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				list = new ArrayList<Integer>();
				list.add(result.getInt("id"));							
			}
			while (result.next())
				list.add(result.getInt("id"));
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
		return list;
	}





	public static List<User> getAllUsers() throws PersistenceException {
		List<User> users = null;
		User user = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select * from users where id < 4000";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				users = new ArrayList<User>();
				user = new User();
				user.setId(result.getLong("id"));
				user.setWeight(1, result.getDouble("1"));
				user.setWeight(2, result.getDouble("2"));
				user.setWeight(3, result.getDouble("3"));
				user.setWeight(4, result.getDouble("4"));
				user.setWeight(5, result.getDouble("5"));
				user.setWeight(6, result.getDouble("6"));
				user.setWeight(7, result.getDouble("7"));
				user.setWeight(8, result.getDouble("8"));
				user.setWeight(9, result.getDouble("9"));
				user.setWeight(10, result.getDouble("10"));
				users.add(user);
			}
			while (result.next()) {
				user = new User();
				user.setId(result.getLong("id"));
				user.setWeight(1, result.getDouble("1"));
				user.setWeight(2, result.getDouble("2"));
				user.setWeight(3, result.getDouble("3"));
				user.setWeight(4, result.getDouble("4"));
				user.setWeight(5, result.getDouble("5"));
				user.setWeight(6, result.getDouble("6"));
				user.setWeight(7, result.getDouble("7"));
				user.setWeight(8, result.getDouble("8"));
				user.setWeight(9, result.getDouble("9"));
				user.setWeight(10, result.getDouble("10"));
				users.add(user);
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
		return users;
	}




	public static List<User> getAllFriendByContext(String context) throws PersistenceException {
		String lat = "";
		String lng = "";

		if (context.equals("Roma")) {
			lat = " v.latitude >= 41.861489 and v.latitude <= 41.9165555 ";
			lng = " v.longitude >= 12.4608979 and v.longitude <= 12.5220048 ";
		}
		if (context.equals("London")) {
			lat = " v.latitude >= 51.4922682 and v.latitude <= 51.5263822 ";
			lng = " v.longitude >= -0.1857303 and v.longitude <= -0.070267 ";
		}
		if (context.equals("Paris")) {
			lat = " v.latitude >= 48.8345925 and v.latitude <= 48.8946835 ";
			lng = " v.longitude >= 2.288955 and v.longitude <= 2.3648768000000002 ";
		}
		if (context.equals("New York")) {
			lat = " v.latitude >= 40.67777779999999 and v.latitude <= 40.8265504 ";
			lng = " v.longitude >= -74.0102778 and v.longitude <= -73.9264769 ";
		}
		if (context.equals("Firenze")) {
			lat = " v.latitude >= 43.7474972 and v.latitude <= 43.787687500000004 ";
			lng = " v.longitude >= 11.228399600000001 and v.longitude <= 11.2693086 ";
		}

		List<User> users = null;
		User user = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select c.user_id, count(c.venue_id) as count"
					+ " from checkins c, venues v"
					+ " where c.venue_id = v.id"
					+ " and " + lat + " and " + lng
					+ " and v.category_fq_id not in (select id from categories where macro_category_id = 5)"
					+ " group by c.user_id"
					+ " order by count DESC"
					+ " limit 6";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				users = new ArrayList<User>();
				user = new User();
				user.setId(result.getInt("c.user_id"));
				users.add(user);
			}
			while (result.next()) {
				user = new User();
				user.setId(result.getInt("c.user_id"));
				users.add(user);
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
		return RetrieveUserWithMoreFriends(users);
	}



	public static List<User> RetrieveUserWithMoreFriends(List<User> list) throws PersistenceException {
		List<User> users = null;
		User user = null;
		String s = "";
		for (User u: list)
			s += " friend_id = " + u.getId() + " or ";
		s = s.substring(0, s.length()-3);
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "SELECT user_id, count(friend_id) as count "
					+ " FROM gscorrdata_new.friendship"
					+ " where " + s
					+ " group by user_id"
					+ " order by count DESC"
					+ " limit 2";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				users = new ArrayList<User>();
				int id = result.getInt("user_id");
				user = RetrieveUserById(id);
				users.add(user);							
			}
			while (result.next()) {
				int id = result.getInt("user_id");
				user = RetrieveUserById(id);
				users.add(user);
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
		return users;
	}



	public static List<Integer> RetrieveUserWithNullUsernameFrom(int from) throws PersistenceException {
		List<Integer> list = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select id from users where username is null and id >= " + from + " order by id limit 60";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				list = new LinkedList<Integer>();
				list.add(result.getInt("id"));							
			}
			while (result.next())
				list.add(result.getInt("id"));
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
		return list;
	}



	public static List<MacroCategory> RetrieveMacroCategoryByUser(int userID) throws PersistenceException {
		List<MacroCategory> macroCategories = null;
		MacroCategory mc = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "SELECT venues.category_fq, macro_category_id"
					+ " FROM checkins, venues, categories"
					+ " where checkins.user_id = " + userID
					+ " and venues.category_fq_id = categories.id"
					+ " and checkins.venue_id = venues.id";
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				macroCategories = new ArrayList<MacroCategory>();
				mc = new MacroCategory();
				mc.setId(result.getInt("macro_category_id"));
				mc.setMacro_category_fq(result.getString("category_fq"));
				macroCategories.add(mc);
			}
			while (result.next()) {
				mc = new MacroCategory();
				mc.setId(result.getInt("macro_category_id"));
				mc.setMacro_category_fq(result.getString("category_fq"));
				macroCategories.add(mc);
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
		return macroCategories;
	}



	public static void aggiornaPesi(List<double[]> pesi) throws PersistenceException {

		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String update = "update users set `1` = ?, `2` = ?, `3` = ?, `4` = ?, `5` = ?, `6` = ?, `7` = ?, `8` = ?, `9` = ?, `10` = ? where id = ?";
			for (double[] d: pesi) {
				statement = connection.prepareStatement(update);
				statement.setFloat(1, (float)d[1]);
				statement.setFloat(2, (float)d[2]);
				statement.setFloat(3, (float)d[3]);
				statement.setFloat(4, (float)d[4]);
				statement.setFloat(5, (float)d[5]);
				statement.setFloat(6, (float)d[6]);
				statement.setFloat(7, (float)d[7]);
				statement.setFloat(8, (float)d[8]);
				statement.setFloat(9, (float)d[9]);
				statement.setFloat(10, (float)d[10]);
				statement.setInt(11, (int)d[0]);				
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



	public static void persistUser(User user) throws PersistenceException {		
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String insert = "insert into users (username, password, gender, age, role, residenceLat, residenceLong, `1`, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `10`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			statement = connection.prepareStatement(insert);
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			statement.setString(3, user.getGender());
			statement.setFloat(4, user.getAge());
			statement.setString(5, user.getRole());
			statement.setString(6, user.getResidenceLat());
			statement.setString(7, user.getResidenceLong());
			statement.setFloat(8, (float)user.getWeigth(1));
			statement.setFloat(9, (float)user.getWeigth(2));
			statement.setFloat(10, (float)user.getWeigth(3));
			statement.setFloat(11, (float)user.getWeigth(4));
			statement.setFloat(12, (float)user.getWeigth(5));
			statement.setFloat(13, (float)user.getWeigth(6));
			statement.setFloat(14, (float)user.getWeigth(7));
			statement.setFloat(15, (float)user.getWeigth(8));
			statement.setFloat(16, (float)user.getWeigth(9));
			statement.setFloat(17, (float)user.getWeigth(10));
//			statement.setInt(18, user.getId());
			statement.executeUpdate();						
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


	public static void updateUsernameAndPassword(List<User> users) throws PersistenceException {

		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String update = "update users set username = ?, password = ? where id = ?";
			for (User u: users) {
				statement = connection.prepareStatement(update);
				statement.setString(1, u.getUsername());
				statement.setString(2, u.getPassword());
				statement.setFloat(3, u.getId());
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



	public static void updateIdPadre(User user, long idPadre) throws PersistenceException {

		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String update = "update users set id_padre = ? where id = ?";
			statement = connection.prepareStatement(update);
			statement.setLong(1, idPadre);
			statement.setLong(2, user.getId());
			statement.executeUpdate();						
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

}