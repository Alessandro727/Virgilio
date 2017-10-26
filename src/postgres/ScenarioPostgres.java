package postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import model.Scenario;

public class ScenarioPostgres {
	
	

	public static void persistScenario(Scenario scenario) throws PersistenceException {
		
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = datasource.getConnection();
			String insert = "insert into scenarios (user_id, context_id, day, hour, food) values (?, ?, ?, ?, ?)";
			statement = connection.prepareStatement(insert);
			statement.setLong(1, scenario.getUser_id());
			statement.setInt(2, scenario.getContext().getId());
			statement.setString(3, scenario.getDay());				
			statement.setString(4, scenario.getHour());
			statement.setBoolean(5, scenario.getFood());
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
