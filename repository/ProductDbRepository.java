package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import exception.ProductRepositoryException;
import model.Product;

public class ProductDbRepository implements ProductRepository {

	private final static String INSERT_PRODUCT_STATEMENT = "INSERT INTO PRODUCTS (QUANTITY, NAME, DESCRIPTION, PRICE, ENTRYDATE, CATEGORY_ID)"
			+ " VALUES (?, ?, ?, ?, ?, ?);";

	// update-statement, the parameters are in the same order like the
	// insert-statement, additional parameter at the end for the id
	private final static String UPDATE_STATEMENT = "UPDATE PRODUCTS SET QUANTITY = ?, NAME = ?, DESCRIPTION = ?, PRICE = ?, ENTRYDATE = ?, CATEGORY_ID = ?"
			+ " WHERE ID = ?";

	// delete-statement has only one parameter -> the id
	private final static String DELETE_PRODUCT_STATEMENT = "DELETE FROM PRODUCTS WHERE ID = ?";

	private final static String QUERY_SELECT_ALL_STATEMENT = "SELECT ID, QUANTITY, NAME, DESCRIPTION, PRICE, ENTRYDATE, CATEGORY_ID FROM PRODUCTS";

	private final static String QUERY_SELECT_BY_ID_STATEMENT = "SELECT ID, NAME, DESCRIPTION, PRICE, CATEGORY_ID, QUANTITY, FROM PRODUCTS WHERE ID = ?";
	
	

	// information for the connection
	private String dbUrl, userName, password;

	// constructor that is getting the fitting information
	public ProductDbRepository(String dbUrl, String userName, String password) {
		this.dbUrl = dbUrl;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public List<Product> selectAll() throws ProductRepositoryException {
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			Statement stmt = conn.createStatement();
			// attention with bigger data amount of data, we are creating an object for
			// every data-set, potentially also 1000000 objects!
			ResultSet result = stmt.executeQuery(QUERY_SELECT_ALL_STATEMENT);
			// list for the return value
			List<Product> allProducts = new ArrayList<>();

			while (result.next()) {
				// reading from the data-set a product-object and adding to the list
				allProducts.add(readProduct(result));
			}
			// returning the product-list
			return allProducts;
		} catch (SQLException e) {
			System.err.println("Error loading the data\n");
			// throwing and forwarding the exception
			throw new ProductRepositoryException("Error on getting access to the DB", e);
		}
	}

	@Override
	public Product selectById(int id) throws ProductRepositoryException {
		// loading the product-data-set from the DB with the specified id
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			// statement with the parameter
			PreparedStatement stmt = conn.prepareStatement(QUERY_SELECT_BY_ID_STATEMENT);
			// setting every value for the parameter (begins with 1)
			stmt.setInt(1, id);
			// executing
			ResultSet result = stmt.executeQuery();
			// throwing exception, when there is no more data-sets
			if (!result.next()) {
				throw new ProductRepositoryException("Product with ID" + id + " doesn't exist");
			}

			Product entity = readProduct(result);
			return entity;

		} catch (Exception e) {
			System.err.println("Error loading a Product-dataset\n");
			e.printStackTrace();
			throw new ProductRepositoryException("Error loading a product dataset", e);
		}
	}

	@Override
	public int insertProduct(Product product) throws ProductRepositoryException {
		// insert into products (quantity, name, description, price, entryDate, category_id)
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {

			// creating statement, returns result-set with the key-values
			PreparedStatement stmt = conn.prepareStatement(INSERT_PRODUCT_STATEMENT, Statement.RETURN_GENERATED_KEYS);

			setCommonParameters(product, stmt);
			// executing the command, result is the count of the affected data-sets
			int rowAffected = stmt.executeUpdate();
			if (rowAffected != 1) {
				System.out.println("No dataset involved with the insert ????");
				throw new ProductRepositoryException("No dataset is involved with the insert.");
			}

			// when everything went right, getting the key
			ResultSet keys = stmt.getGeneratedKeys();
			if (keys.next()) {
				int id = keys.getInt(1);
				System.out.printf("Object inserted with the new ID=%d\n", id);
				return id;
			} else {
				// shouldn't happen
				System.err.printf("Object inserted, new ID unknown!");
				throw new ProductRepositoryException("The dataset was inserted but the ID couldn't be identified");
			}
		} catch (SQLException e) {
			System.err.println("Error when inserting product datatset");
			e.printStackTrace();
			throw new ProductRepositoryException("Error inserting the product-datasetz", e);
		}
	}

	@Override
	public void updateProduct(Product product) throws ProductRepositoryException {
		// changing a product

		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT);
			// setting the parameters
			setCommonParameters(product, stmt);
			// setting the id, the seventh parameter
			stmt.setInt(7, product.getId());
			int count = stmt.executeUpdate();
			// when no data-set was affected, then the data-set doesn't exist
			if (count == 0) {
				throw new ProductRepositoryException("Product with the ID " + product.getId() + " doesn't exist");
			}

		} catch (Exception e) {
			System.err.println("Error during updating a product dataset");
			e.printStackTrace();
			throw new ProductRepositoryException("Error during updating a product dataset", e);
		}

	}

	@Override
	public void deleteProduct(int id) throws ProductRepositoryException {
		// delete a product
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			PreparedStatement stmt = conn.prepareStatement(DELETE_PRODUCT_STATEMENT);
			// setting the id
			stmt.setInt(1, id);
			// executing
			int count = stmt.executeUpdate();
			// when no data-set was affected, then the product doesn't exist
			if (count == 0) {
				throw new ProductRepositoryException("Product with ID " + id + " doesn't exist");
			}
		} catch (Exception e) {
			System.err.println("Error during deleting a product dataset");
			e.printStackTrace();
			throw new ProductRepositoryException("Error during deleting a product dataset", e);
		}

	}

	// order of the parameters: quantity, name, description, price, entryDate, category_id
	private void setCommonParameters(Product product, PreparedStatement stmt) throws SQLException {
		// setting the name, first parameter, second parameter and etc.
		stmt.setInt(1, product.getQuantity());
		stmt.setString(2, product.getName());
		stmt.setString(3, product.getDescription());
		stmt.setDouble(4, product.getPrice());
		stmt.setDate(5, Date.valueOf(product.getEntryDate()));
		stmt.setInt(6, product.getCategoryId());
	}

	private Product readProduct(ResultSet result) throws SQLException {
		Product entity = new Product();
		entity.setId(result.getInt("id"));
		entity.setName(result.getString("name"));
		entity.setDescription(result.getString("description"));
		entity.setPrice(result.getDouble("price"));
		entity.setEntryDate(result.getDate("entryDate").toLocalDate());
		entity.setQuantity(result.getInt("quantity"));
		entity.setCategoryId(result.getInt("category_id"));

		return entity;
	}

}
