package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import common.MessageBox;
import exception.CategoryRepositoryException;
import exception.ProductRepositoryException;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import model.Category;
import service.CategoryAndProductTableService;

public class CategoryDbRepository implements CategoryRepository {

	private final static String INSERT_CATEGORY_STATEMENT = "insert into Categories (name) values (?)";

	private final static String UPDATE_STATEMENT = "update Categories set name = ? where id = ?";

	private final static String DELETE_CATEGORY_STATEMENT = "delete from Categories where id = ?";

	private final static String QUERY_SELECT_ALL = "select id, name from Categories";

	private final static String QUERY_SELECT_BY_ID = "select id, name from Categories where id = ?";

	private final static String QUERY_SELECT_BY_NAME = "select id, name from Categories where name = '?'";

	private String dbUrl, userName, password;

	public CategoryDbRepository(String dbUrl, String userName, String password) {
		this.dbUrl = dbUrl;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public List<Category> selectAll() throws CategoryRepositoryException {
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			Statement stmt = conn.createStatement();
			// attention with bigger data amount of data, we are creating an object for
			// every data-set, potentially also 1000000 objects!
			ResultSet result = stmt.executeQuery(QUERY_SELECT_ALL);
			// list for the return value
			List<Category> allCategories = new ArrayList<>();

			while (result.next()) {
				// reading from the data-set a category-object and adding to the list
				allCategories.add(readCategory(result));
			}
			// returning the category-list
			return allCategories;
		} catch (SQLException e) {
			System.err.println("Error loading the data\n");

			throw new CategoryRepositoryException("Error on getting access to the DB", e);
		}
	}

	@Override
	public Category selectById(int id) throws CategoryRepositoryException {
		// loading the category-data-set from the DB with the specified id
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			// statement with the parameter
			PreparedStatement stmt = conn.prepareStatement(QUERY_SELECT_BY_ID);
			// setting every value for the parameter (begins with 1)
			stmt.setInt(1, id);
			// executing
			ResultSet result = stmt.executeQuery();
			// throwing exception, when there is no more data-sets
			if (!result.next()) {
				throw new CategoryRepositoryException("Category with ID" + id + " doesn't exist\n");
			}

			Category entity = readCategory(result);
			return entity;

		} catch (Exception e) {
			System.err.println("Error loading a catagory dataset");
			e.printStackTrace();
			throw new CategoryRepositoryException("Error loading a category dataset", e);
		}
	}

	@Override
	public Category selectByName(String name) throws CategoryRepositoryException {
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			// statement with the parameter
			PreparedStatement stmt = conn.prepareStatement(QUERY_SELECT_BY_NAME);
			// setting every value for the parameter (begins with 1)
			stmt.setString(1, name);
			// executing
			ResultSet result = stmt.executeQuery();
			// throwing exception, when there is no more data-sets
			if (!result.next()) {
				throw new CategoryRepositoryException("Category with name" + name + " doesn't exist");
			}

			Category entity = readCategory(result);
			return entity;

		} catch (Exception e) {
			System.err.println("Error loading a catagory dataset");
			e.printStackTrace();
			throw new CategoryRepositoryException("Error loading a category dataset", e);
		}
	}

	@Override
	public int insertCategory(Category category) throws CategoryRepositoryException {
		// insert into categories (name)
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {

			// creating statement, that returns a a result-set with new key-values after the
			// result
			PreparedStatement stmt = conn.prepareStatement(INSERT_CATEGORY_STATEMENT, Statement.RETURN_GENERATED_KEYS);

			setCommonParameters(category, stmt);
			// executing the command, result is the count of affected data-sets -> is 1 when
			// everything is okay
			int rowAffected = stmt.executeUpdate();
			if (rowAffected != 1) {
				System.out.println("No dataset involved with the insert ????");
				throw new CategoryRepositoryException("No dataset is involved with the insert.");
			}

			// when operation was successful, get the key
			ResultSet keys = stmt.getGeneratedKeys();
			if (keys.next()) {
				int id = keys.getInt(1);
				System.out.printf("Object inserted with the new ID=%d\n", id);
				return id;
			} else {
				// should not happen
				System.err.printf("Object inserted, new ID unknown!");
				throw new CategoryRepositoryException("The dataset was inserted but the ID couldn't be identified");
			}
		} catch (SQLException e) {
			System.err.println("Error when inserting category datatset");
			e.printStackTrace();
			throw new CategoryRepositoryException("Error inserting the category dataset", e);
		}
	}

	@Override
	public void updateCategory(Category category) throws CategoryRepositoryException {
		// changing a category
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT);
			// setting the parameters
			setCommonParameters(category, stmt);
			// setting the id (second argument in the categories table)
			stmt.setInt(2, category.getId());
			int count = stmt.executeUpdate();
			// if no data-set is affected, then the category data-set is not existing
			// anymore
			if (count == 0) {
				throw new ProductRepositoryException("Category with the ID " + category.getId() + " doesn't exist");
			}

		} catch (Exception e) {
			System.err.println("Error during updating a category dataset");
			e.printStackTrace();
			throw new CategoryRepositoryException("Error during updating a category dataset", e);
		}

	}

	@Override
	public void deleteCategory(int id) throws CategoryRepositoryException {
		// delete the category
		try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)) {
			// the service for checking if category is used
			CategoryAndProductTableService service = new CategoryAndProductTableService();
			service.setConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);

			PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY_STATEMENT);
			// setting the id
			stmt.setInt(1, id);

			System.out.println("id: " + id);

			// check if the category is used by any product
			if (service.checkIfCategoryUsedInProducts(id)) {
				MessageBox.show("Deleting Category", "A product is linked with the category", AlertType.INFORMATION,
						ButtonType.OK);
			} else {

				// executing
				int count = stmt.executeUpdate();
				// when no data-set is affected, the category data-set doesn't exist anymore
				if (count == 0) {
					throw new CategoryRepositoryException("Category with ID " + id + " doesn't exist");
				}
			}
		} catch (Exception e) {
			System.err.println("Error during deleting a category dataset");
			e.printStackTrace();
			throw new CategoryRepositoryException("Error during deleting a category dataset", e);
		}

	}

	private void setCommonParameters(Category category, PreparedStatement stmt) throws SQLException {
		// the parameter, setting the name
		stmt.setString(1, category.getName());
	}

	private Category readCategory(ResultSet result) throws SQLException {
		Category entity = new Category();
		// setting the value in the property
		entity.setId(result.getInt("id"));
		entity.setName(result.getString("name"));

		return entity;
	}

}
