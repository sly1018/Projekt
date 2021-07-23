package controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Product;

public class EditProductWindow {

	private Product editProduct;

	public EditProductWindow() {
	}

	public EditProductWindow(Product product) {
		this.editProduct = product;
	}

	public Product showModal() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/products.fxml"));
		Parent root = loader.load();

		Scene scene = new Scene(root, 450, 500);

		// creating stage for the scene
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setScene(scene);
		// adapt to add/edit
		EditProductsController controller = loader.getController();
		controller.setProduct(editProduct);
		stage.setTitle(editProduct != null ? "Edit product" : "New product");
		// as long as this window is open, no other window of the application can be
		// focused
		stage.initModality(Modality.APPLICATION_MODAL);
		// show and wait until closed
		stage.showAndWait();

		Product result = controller.getProduct();

		return result;
	}

}
