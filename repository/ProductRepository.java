package repository;

import java.util.List;

import exception.ProductRepositoryException;
import model.Product;

public interface ProductRepository {
	/**
	 * Loading all products.
	 * 
	 * @return A list of products
	 * @throws ProductRepositoryException
	 */
	List<Product> selectAll() throws ProductRepositoryException;

	/**
	 * Loading a product with the id of the product.
	 * 
	 * @param id of the product
	 * @return A product-object
	 * @throws ProductRepositoryException
	 */
	Product selectById(int id) throws ProductRepositoryException;

	/**
	 * Add a product.
	 * 
	 * @param product, a product-object, that will be added.
	 * @return Id of the product, that was added.
	 * @throws ProductRepositoryException
	 */
	int insertProduct(Product product) throws ProductRepositoryException;

	/**
	 * A product will be updated.
	 * 
	 * @param product, the product-object, that will be updated.
	 * @throws ProductRepositoryException
	 */
	void updateProduct(Product product) throws ProductRepositoryException;

	/**
	 * Delete a product.
	 * 
	 * @param id of the product that will be deleted.
	 * @throws ProductRepositoryException
	 */
	void deleteProduct(int id) throws ProductRepositoryException;

}
