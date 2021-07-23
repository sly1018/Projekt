package repository;

public class Constants {

	// connection to the db
	private static final String DB_URL = "jdbc:mariadb://localhost/productDB";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "";
	
	public static String getDbUrl() {
		return DB_URL;
	}
	public static String getUsername() {
		return USERNAME;
	}
	public static String getPassword() {
		return PASSWORD;
	}

}
