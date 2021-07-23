package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import model.Category;
import model.Product;
import repository.Constants;

public class CreateDatabase {

	// creating the db, with UTF8 encoding
	private static final String CREATE_PRODUCT_DB = "CREATE DATABASE PRODUCTDB CHARACTER SET UTF8MB4 COLLATE UTF8MB4_UNICODE_CI;";

	// creating the table
	private static final String CREATE_CATEGORY_TABLE = "CREATE TABLE CATEGORIES ("
			+ "	ID INT NOT NULL AUTO_INCREMENT KEY, NAME  VARCHAR(150)  NOT NULL);";
	private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE PRODUCTS ("
			+ " ID INT NOT NULL AUTO_INCREMENT KEY," + " QUANTITY INT NOT NULL," + " NAME  VARCHAR(150)  NOT NULL,"
			+ " DESCRIPTION  VARCHAR(1000)  NOT NULL," + " PRICE DOUBLE NOT NULL," + " ENTRYDATE DATE NOT NULL,"
			+ " CATEGORY_ID INT NOT NULL," + " FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES(ID)" + ")";

	// dropping the DB
	private static final String DROP_WHOLE_DATABASE = "DROP DATABSE PRODUCTDB";

	// dropping the tables
	private static final String DROP_CATEGORY_TABLE = "DROP TABLE CATEGORIES";
	private static final String DROP_PRODUCT_TABLE = "DROP TABLE PRODUCTS";

	public static void main(String[] args) throws IOException {
		// here the methods for createTable, dropTable and insertSampleData will be
		// called up

		// connection to the DB
		try {
			Connection con = DriverManager.getConnection(Constants.getDbUrl(), Constants.getUsername(),
					Constants.getPassword());
			System.out.println("Connection established");

			// drop the table including the data
			dropProductTable(con);
			dropCategoryTable(con);

			// System.out.println("The tables Categories and Products have been dropped");

			// creating the categories and products tables
			createCategoriesTable(con);
			createProductsTable(con);

			System.out.println("The tables for categories and products have been created");

			// creating objects for sample data for the tables categories and products
			Category c1 = new Category("Technic");
			Category c2 = new Category("Carpets");
			Category c3 = new Category("Furniture");

			Product p1 = new Product(99, "Samsung", "Smartphone with One UI Core Version as OS", 139.99,
					LocalDate.of(2021, 07, 06), 1);
			Product p2 = new Product(5, "Mauri", "Carpet from Afghanistan, handcrafted", 10000.00,
					LocalDate.of(2021, 07, 10), 2);
			Product p3 = new Product(1, "Chair", "Antique chair refurbished, good for reading books", 49.99,
					LocalDate.of(2021, 07, 10), 3);

			// inserting the objects into the tables
			insertCategory(c1, con);
			insertCategory(c2, con);
			insertCategory(c3, con);
			insertProduct(p1, con);
			insertProduct(p2, con);
			insertProduct(p3, con);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// dropping product table
	private static void dropProductTable(Connection con) throws SQLException {

		// statement
		Statement stmt = con.createStatement();

		// sending the statement to RDBMS
		stmt.execute(DROP_PRODUCT_TABLE);

	}

	// dropping category table
	private static void dropCategoryTable(Connection con) throws SQLException {

		// statement
		Statement stmt = con.createStatement();

		// sending the statement to RDBMS
		stmt.execute(DROP_CATEGORY_TABLE);

	}

	private static void createCategoriesTable(Connection con) throws SQLException {

		Statement stmt = con.createStatement();

		stmt.execute(CREATE_CATEGORY_TABLE);
	}

	private static void createProductsTable(Connection con) throws SQLException {

		Statement stmt = con.createStatement();

		stmt.execute(CREATE_PRODUCT_TABLE);
	}

	private static void insertCategory(Category c, Connection con) throws SQLException {

		PreparedStatement pStmt = con.prepareStatement("insert into categories (name) values (?)");

		pStmt.setString(1, c.getName());

		int rowsChanged = pStmt.executeUpdate();
		System.out.println("Rows changed: " + rowsChanged);

	}

	private static void insertProduct(Product p, Connection con) throws SQLException {

		PreparedStatement pStmt = con.prepareStatement(
				"insert into products (quantity, name, description, price, entryDate, category_id) values (?, ?, ?, ?, ?, ?);");

		pStmt.setInt(1, p.getQuantity());
		pStmt.setString(2, p.getName());
		pStmt.setString(3, p.getDescription());
		pStmt.setDouble(4, p.getPrice());
		pStmt.setDate(5, Date.valueOf(p.getEntryDate()));
		pStmt.setInt(6, p.getCategoryId());

		int rowsChanged = pStmt.executeUpdate();
		System.out.println("Rows changed: " + rowsChanged);

	}

}
