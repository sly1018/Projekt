package controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import repository.Constants;

public class CategoryTableWindow {

	public void showModal() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CategoryTableView.fxml"));
		Parent root = loader.load();

		Scene scene = new Scene(root, 350, 500);

		// getting the controller
		CategoryTableController controller = loader.getController();
		// setting the repository
		controller.setConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);

		// creating stage for the scene
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setScene(scene);

		// getting and setting the stylesheet
		scene.getStylesheets().add(getClass().getResource("/views/productStyles.css").toExternalForm());
		stage.setTitle("Categories Table");
		// as long as this window is open, no other window of the application can be
		// focused
		stage.initModality(Modality.APPLICATION_MODAL);
		// show and wait until closed
		stage.showAndWait();

	}

}
