package postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.Venue;



public class CategoryPostgres {
	
	
	// uso i venue per memorizzare le categorie
	public static List<Venue> getCategories(int from, int to) throws PersistenceException {
		List<Venue> venues = null;
		Venue venue = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select *"
					+ " from categories"
					+ " where id >= " + from + " and id <= " + to;
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				venues = new LinkedList<Venue>();
				venue = new Venue();
				venue.setId(result.getInt("id"));
				venue.setCategory_fq(result.getString("category_fq"));				
				venues.add(venue);				
			}
			while (result.next()) {				
				venue = new Venue();
				venue.setId(result.getInt("id"));
				venue.setCategory_fq(result.getString("category_fq"));				
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
	
	
	
	

	public static void updateCategoryFqId(List<Venue> venues) throws PersistenceException {
		
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String update = "update venues set category_fq_id = ? where category_fq = ?";
			for (Venue v: venues) {
				statement = connection.prepareStatement(update);
				statement.setInt(1, v.getId());
				statement.setString(2, v.getCategory_fq());
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
	
	
	

	

}
