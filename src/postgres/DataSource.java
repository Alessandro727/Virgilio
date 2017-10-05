package postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
	private String driver = "com.mysql.jdbc.Driver";
//	private String driver = "org.postgresql.Driver";
	private String dbName = "dbTesi";
//	private String dbName = "gScorrData";
	private String dbURI = "jdbc:mysql://localhost/"+dbName;
	//private String dbURI = "jdbc:postgresql://localhost/gScorrData";
	private String userName = "root";
	//private String userName = "postgres";
	private String password = "gigiotto";

	public Connection getConnection() throws PersistenceException {
		Connection connection;
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