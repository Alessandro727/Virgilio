package postgres;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FromCSVtoDB {
	
	
	public static void InsertCheckins(String csvFile) throws PersistenceException {
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String[] checkin;
		
		String insert = "";
		DataSource datasource = new DataSource();
		Connection connection = null;
		PreparedStatement statement = null;
		int i = 1000001;
	 
		
		try {

			br = new BufferedReader(new FileReader(csvFile));
			connection = datasource.getConnection();
			Date data;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			while ((line = br.readLine()) != null) {
				
				//use comma as separator
				checkin = line.split(cvsSplitBy);
								
				insert = "insert into checkins (id, user_id, location_id, latitude, longitude, date) values (?, ?, ?, ?, ?, ?)";
				statement = connection.prepareStatement(insert);
				
				statement.setInt(1, i);	// id
				statement.setInt(2, Integer.parseInt(checkin[0]));	//  user_id				
				statement.setInt(3, Integer.parseInt(checkin[4]));	// location_id
				statement.setBigDecimal(4, new BigDecimal(checkin[1]));	// latitude
				statement.setBigDecimal(5, new BigDecimal(checkin[2]));	// longitude
				data = df.parse(checkin[3]);	// date
				statement.setTimestamp(6, new java.sql.Timestamp(data.getTime()));					
					
				statement.executeUpdate();
				System.out.println(i);
				i++;
	 
			}
	 
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} catch (PersistenceException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
				if (br != null)
					br.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	 
		System.out.println("Done");
	}

}