package postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import model.Context;

public class ContextPostgres {
	
	
	public static Context RetrieveRandomContext() throws PersistenceException {
		Random random = new Random();
		//nextInt(int n): ritorna un numero pseudocasuale tra 0 (incluso) ed n (escluso)
	    int randomNum = random.nextInt(5) + 1;
		
		Context context = null;
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = datasource.getConnection();
			String query = "select * from contexts where id = " + randomNum; 
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			if (result.next()) {
				context = new Context();
				context.setId(randomNum);
				context.setMeteo(result.getString("meteo"));
				context.setMode(result.getString("mode"));
				context.setCity(result.getString("city"));
				context.setStart(result.getString("start"));
				context.setEnd(result.getString("end"));
				context.setTime(result.getInt("time"));
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
		return context;
	}
	

}
