package service;

import java.util.List;

import common.MessageBox;
import exception.ProductRepositoryException;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import model.Product;
import repository.CategoryDbRepository;
import repository.CategoryRepository;
import repository.ProductDbRepository;
import repository.ProductRepository;

public class CategoryAndProductTableService {

	// objects for both repositories
	ProductRepository prodRepo;
	CategoryRepository catRepo;

	// initializing the repositories
	public void setConnection(String dbUrl, String username, String password) {
		prodRepo = new ProductDbRepository(dbUrl, username, password);
		catRepo = new CategoryDbRepository(dbUrl, username, password);
	}

	// method for checking if category is used in products
	public boolean checkIfCategoryUsedInProducts(Integer id) {

		boolean retVal = false;

		try {
			List<Product> allProducts = prodRepo.selectAll();

			for (Product product : allProducts) {
				if (product.getCategoryId() == id) {
					retVal = true;
					System.out.println("retVal: " + retVal);
				}
			}

		} catch (ProductRepositoryException e) {
			e.printStackTrace();
			MessageBox.show("Error", "Error occured during checking" + e.getMessage(), AlertType.ERROR, ButtonType.OK);
		}
		return retVal;
	}

}
