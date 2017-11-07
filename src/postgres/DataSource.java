package postgres;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DataSource {
	private final String driver = "com.mysql.jdbc.Driver";
	//	private String driver = "org.postgresql.Driver";
	private final String dbName = "dbVirgilio";
	//	private String dbName = "gScorrData";
	private final String dbURI = "jdbc:mysql://localhost/"+dbName;
	//private String dbURI = "jdbc:postgresql://localhost/gScorrData";
	private final String userName = "root";
	//private String userName = "postgres";


	public Connection getConnection() throws PersistenceException {
		Connection connection;

		String password = null; 

		InputStream inputStream = 
				getClass().getClassLoader().getResourceAsStream("config.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream ));


		String sCurrentLine;

		try {
			while ((sCurrentLine = reader.readLine()) != null) {
				if (sCurrentLine.contains("MYSQL_PASSWORD"))	{
					password =  sCurrentLine.split("MYSQL_PASSWORD=")[1];
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		try {
			Class.forName(driver);
			//Class.forName("org.postgresql.Driver");		//driver class loading
			/*
			 * Now the driver is registered at DriverManager (postgreSQL driver automatically
			 * registers itself at DriverManager once his class is loaded).
			 * Since driver is loaded and registered, I can obtain a connection to the database 
			 */
			connection = DriverManager.getConnection(dbURI, userName, password);
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			//			System.out.println("Where is your postgreSQL JDBC Driver?");
			e.printStackTrace();
			throw new PersistenceException(e.getMessage());
		} catch(SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		return connection;
	}



}