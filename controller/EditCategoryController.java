package controller;

import common.MessageBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Category;

public class EditCategoryController {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Button btnOk;

	@FXML
	private Button btnCancel;

	// the category which is edited
	private Category categoryResult;

	@FXML
	public void initialize() {
		System.out.println("initializing...");

		checkValid();

		txtName.textProperty().addListener((o, oldval, newval) -> checkValid());
	}

	public void setCategory(Category ediCategory) {
		// the values from the object
		if (ediCategory != null) {
			txtId.setText(Integer.toString(ediCategory.getId()));
			txtName.setText(ediCategory.getName());
		}
	}

	public Category getCategory() {
		return categoryResult;
	}

	// Event Listener on Button[#btnOk].onAction
	@FXML
	public void onOk(ActionEvent event) {
		try {
			categoryResult = new Category();
			// setting the value from the controls
			if (txtId.getText() != null && !txtId.getText().isEmpty()) {
				categoryResult.setId(Integer.parseInt(txtId.getText()));
			}
			
			categoryResult.setName(txtName.getText());

			// close the stage
			((Stage) txtId.getScene().getWindow()).close();
		} catch (Exception e) {
			// the category-object can not be used
			categoryResult = null;
			e.printStackTrace();
			MessageBox.show("Recording", "Error on recording" + e.getMessage(), AlertType.ERROR, ButtonType.OK);
		}
	}

	// Event Listener on Button[#btnCancel].onAction
	@FXML
	public void onCancel(ActionEvent event) {
		categoryResult = null;
		// close the stage
		((Stage) txtId.getScene().getWindow()).close();
	}

	private void checkValid() {
		boolean valid = isInputValid();
		if (valid) {
			btnOk.setDisable(false);
		} else {
			btnOk.setDisable(true);
		}
	}

	private boolean isInputValid() {
		boolean valid = txtName.getText() != null && !txtName.getText().isBlank();

		return valid;
	}
}
