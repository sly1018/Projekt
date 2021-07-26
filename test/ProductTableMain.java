package test;

import controller.ProductTableController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.Constants;

public class ProductTableMain extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProductTableView.fxml"));
		Parent root = loader.load();

		// getting the controller
		ProductTableController controller = loader.getController();
		// setting the repository
		controller.setConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);

		Scene scene = new Scene(root, 500, 500);
		// setting the CSS-Stylesheet and loading it
		scene.getStylesheets().add(getClass().getResource("/views/productStyles.css").toExternalForm());
		// show it
		primaryStage.setScene(scene);
		primaryStage.setTitle("TableView");
		primaryStage.show();

	}

}
