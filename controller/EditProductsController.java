package controller;

import java.io.IOException;
import java.util.List;

import common.MessageBox;
import exception.CategoryRepositoryException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Category;
import model.Product;
import repository.Constants;

public class EditProductsController {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtQuantity;

	@FXML
	private TextField txtPrice;

	@FXML
	private DatePicker dtpEntrydate;

	@FXML
	private ComboBox<Category> cmbType;

	@FXML
	private TextArea txtComment;

	@FXML
	private Button btnCategory;

	@FXML
	private Button btnOk;

	@FXML
	private Button btnCancel;

	// the product which is edited
	private Product productResult;

	// for the list of categories
	private CategoryTableController catController = new CategoryTableController();

	List<Category> categories;

	@FXML
	public void initialize() {
		System.out.println("initializing...");

		loadCategories();

		checkValid();

		txtName.textProperty().addListener((o, oldval, newval) -> checkValid());
		txtQuantity.textProperty().addListener((o, oldval, newval) -> checkValid());
		txtPrice.textProperty().addListener((o, oldval, newval) -> checkValid());
		dtpEntrydate.valueProperty().addListener((o, oldval, newval) -> checkValid());
		cmbType.valueProperty().addListener((o, oldval, newval) -> checkValid());

	}

	// method for loading the categories and filling up the list of categories
	public void loadCategories() {
		// calling the controller to fill the list of categories
		catController.setConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);
		categories = catController.getAllCategories();

		for (Category category : categories) {
			cmbType.getItems().add(category);
		}
	}

	public void setProduct(Product editProduct) {
		// the values from the object
		if (editProduct != null) {
			txtId.setText(Integer.toString(editProduct.getId()));
			txtName.setText(editProduct.getName());
			txtQuantity.setText(Integer.toString(editProduct.getQuantity()));
			txtPrice.setText(Double.toString(editProduct.getPrice()));
			dtpEntrydate.setValue(editProduct.getEntryDate());
			txtComment.setText(editProduct.getDescription());

			try {
				cmbType.setValue(catController.getCategoryById(editProduct.getCategoryId()));
			} catch (CategoryRepositoryException e) {
				MessageBox.show("Loading Category",
						"Error occured during setting the category value\n" + e.getMessage(), AlertType.ERROR,
						ButtonType.OK);
				e.printStackTrace();
			}
		}
	}

	public Product getProduct() {
		return productResult;
	}

	// Event Listener on Button[#btnOk].onAction
	@FXML
	public void onOk(ActionEvent event) {
		try {
			productResult = new Product();
			// setting the values from the controls
			if (txtId.getText() != null && !txtId.getText().isEmpty()) {
				productResult.setId(Integer.parseInt(txtId.getText()));
			}

			productResult.setName(txtName.getText());
			productResult.setQuantity(Integer.parseInt(txtQuantity.getText()));
			productResult.setPrice(Double.parseDouble(txtPrice.getText()));
			productResult.setEntryDate(dtpEntrydate.getValue());
			productResult.setDescription(txtComment.getText());
			productResult.setCategoryId(cmbType.getValue().getId());

			// close the stage
			((Stage) txtId.getScene().getWindow()).close();
		} catch (Exception e) {
			// the product-object can not be used
			productResult = null;
			e.printStackTrace();
			MessageBox.show("Recording", "Error occurred during recording" + e.getMessage(), AlertType.ERROR,
					ButtonType.OK);
		}
	}

	// Event Listener on Button[#btnCancel].onAction
	@FXML
	public void onCancel(ActionEvent event) {
		productResult = null;
		// close the stage
		((Stage) txtId.getScene().getWindow()).close();
	}

	// Event Listener on Button[#onAddCategory].onAction
	@FXML
	public void onAddCategory(ActionEvent event) throws IOException {
		CategoryTableWindow ctw = new CategoryTableWindow();
		ctw.showModal();
		loadCategories();
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
		boolean valid = txtName.getText() != null && !txtName.getText().isBlank() && txtQuantity.getText() != null
				&& !txtQuantity.getText().isBlank() && txtPrice.getText() != null && !txtPrice.getText().isBlank()
				&& dtpEntrydate.getValue() != null && cmbType.getValue() != null && txtComment.getText() != null
				&& !txtComment.getText().isBlank();

		System.out.printf("Check Valid: isValid = %s\n", valid);
		return valid;
	}

}
