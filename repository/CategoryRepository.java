package repository;

import java.util.List;

import exception.CategoryRepositoryException;
import model.Category;

public interface CategoryRepository {
	/**
	 * Loading all categories.
	 * 
	 * @return a list of categories
	 * @throws CategoryRepositoryException
	 */
	List<Category> selectAll() throws CategoryRepositoryException;

	/**
	 * Loading a category with the id of the category.
	 * 
	 * @param id of the category
	 * @return A category-object
	 * @throws CategoryRepositoryException
	 */
	Category selectById(int id) throws CategoryRepositoryException;

	/**
	 * Loading a category with the name of the category.
	 * 
	 * @param name of the category
	 * @return A category-object
	 * @throws CategoryRepositoryException
	 */
	Category selectByName(String name) throws CategoryRepositoryException;

	/**
	 * Add a category.
	 * 
	 * @param category, a category-object, that will be added.
	 * @return Id of the category, that was added.
	 * @throws CategoryRepositoryException
	 */
	int insertCategory(Category category) throws CategoryRepositoryException;

	/**
	 * A category will be updated.
	 * 
	 * @param category, the category-object, that will be updated.
	 * @throws CategoryRepositoryException
	 */
	void updateCategory(Category category) throws CategoryRepositoryException;

	/**
	 * Delete a category.
	 * 
	 * @param id of the category that will be deleted.
	 * @throws CategoryRepositoryException
	 */
	void deleteCategory(int id) throws CategoryRepositoryException;

}
