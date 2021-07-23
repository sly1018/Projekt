package controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Category;

public class EditCategoryWindow {

	private Category editCategory;

	public EditCategoryWindow() {
	}

	public EditCategoryWindow(Category category) {
		this.editCategory = category;
	}

	public Category showModal() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/category.fxml"));
		Parent root = loader.load();

		Scene scene = new Scene(root, 450, 500);

		// creating stage for the scene
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setScene(scene);
		// adapt to add/edit
		EditCategoryController controller = loader.getController();
		controller.setCategory(editCategory);
		stage.setTitle(editCategory != null ? "Edit category" : "New category");
		// as long as this window is open, no other window of the application can be
		// focused
		stage.initModality(Modality.APPLICATION_MODAL);
		// show and wait until closed
		stage.showAndWait();

		Category result = controller.getCategory();

		System.out.println("result: " + result);

		return result;
	}

}
