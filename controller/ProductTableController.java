package controller;

import common.MessageBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.Product;
import repository.ProductDbRepository;
import repository.ProductRepository;

public class ProductTableController {

	private ProductRepository repository;

	@FXML
	private Button btnAdd;

	@FXML
	private Button btnEdit;

	@FXML
	private Button btnDelete;

	@FXML
	private Button btnReload;

	@FXML
	private TableView<Product> tblProducts;

	@FXML
	VBox boxProduct;

	@FXML
	private Label lblComment;

	@FXML
	private TableColumn<Product, Integer> colId;

	@FXML
	private TableColumn<Product, String> colName;

	@FXML
	private TableColumn<Product, Double> colPrice;

	@FXML
	private TableColumn<Product, Integer> colQuantity;

	// TODO: Integer in String umwandeln
	@FXML
	private TableColumn<Product, Integer> colCat;

	// for the binding
	private ListProperty<Product> products;

	// binding of the selected product to an ObjectProperty
	private ObjectProperty<Product> selectedProduct;

	public ProductTableController() {
		// creating the properties for the binding
		products = new SimpleListProperty<>();
		selectedProduct = new SimpleObjectProperty<>();
	}

	// Getter
	public Product getSelectedProduct() {
		return selectedProduct.get();
	}

	// Setter
	public void setSelectedProduct(Product selProduct) {
		selectedProduct.set(selProduct);
	}

	public ObjectProperty<Product> selectedProductProperty() {
		return selectedProduct;
	}

	// initializing the repository
	public void setConnection(String dbUrl, String username, String password) {
		repository = new ProductDbRepository(dbUrl, username, password);
		reload();
	}

	@FXML
	private void initialize() {

		System.out.println("Product List initialize");

		// Handler for changes in the selection view in ListView
		tblProducts.getSelectionModel().selectedItemProperty().addListener(
				// setting the ObjectProperty as Product-Object
				(o, oldPrd, newPrd) -> {
					// setting the Product-Object in Object-Property
					// trigger a changed-event from the property
					selectedProduct.set(newPrd);
				});

		// binding of the disable-properties
		// buttons will be disabled when no product is selected
		btnEdit.disableProperty().bind(Bindings.isNull(selectedProduct));
		btnDelete.disableProperty().bind(Bindings.isNull(selectedProduct));
		// setting the visibility property
		// Box will be shown when a product is selected
		boxProduct.visibleProperty().bind(Bindings.isNotNull(selectedProduct));

		// binding the text-property of the label
		lblComment.textProperty().bind(Bindings.selectString(selectedProduct, "description"));

		// cell-value-factories for the columns
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		colCat.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
		
		// showing the string of category 
		// TODO: colCat.setCellValueFactory(this::createCategoryCell)
		colCat.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product,Integer>, ObservableValue<Integer>>() {
			// wird immer aufgerufen wenn eine TableCell von colCat erzeugt wird
			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Product, Integer> param) {
				// TODO Auto-generated method stub
				return null;
			}
			// rückgabe ein String
		});

		// the items in the table will be updated automatically when ObservableList in
		// the property changes
		tblProducts.itemsProperty().bind(products);

	}

	@FXML
	public void addProduct() {
		try {// showing the dialog without the object
			EditProductWindow dlg = new EditProductWindow();
			Product entity = dlg.showModal();
			if (entity != null) {
				System.out.println("New Product: " + entity);
				repository.insertProduct(entity);
				reload();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("New product", "Error occured" + e.getMessage(), AlertType.ERROR, ButtonType.OK);
		}
	}

	@FXML
	public void editProduct() {
		try {
			// new local variable
			Product selProduct = getSelectedProduct();
			// editing the selected product
			System.out.println("Selected product: " + selProduct);
			// showing the dialog with the selected product
			EditProductWindow dlg = new EditProductWindow(selProduct);
			Product entity = dlg.showModal();
			if (entity != null) {
				System.out.println("Changed product: " + entity);

				repository.updateProduct(entity);

				// get the index of the original-object
				int index = products.indexOf(getSelectedProduct());
				System.out.println("index: " + index);
				// setting the changed object into the index
				products.set(index, entity);

			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("Edit product", "Error occured during recording" + e.getMessage(), AlertType.ERROR,
					ButtonType.OK);
		}
	}

	@FXML
	public void deleteProduct() {
		try {
			System.out.println("Delete product: " + getSelectedProduct());

			// delete in the repository
			repository.deleteProduct(getSelectedProduct().getId());
			reload();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("Delete product", "Error occurred during deleting" + e.getMessage(), AlertType.ERROR,
					ButtonType.OK);
		}
	}

	@FXML
	public void reload() {
		try {
			System.out.println("Reloading...");

			this.products.set(FXCollections.observableArrayList(repository.selectAll()));
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("Reload", "Error occured during loading" + e.getMessage(), AlertType.ERROR, ButtonType.OK);
		}
	}

	@FXML
	public void onClicked(MouseEvent me) {
		System.out.println("Mouse clicked");

		// When the mouse is clicked twice
		if (me.getClickCount() == 2) {
			editProduct();
		}
	}

	@FXML
	public void onKey(KeyEvent ke) {
		System.out.printf("Key pressed KeyCode=%s\n", ke.getCode());

		switch (ke.getCode()) {
		case ENTER -> editProduct();
		case DELETE -> {
			if (MessageBox.show("Delete the product", "Delete the selected dataset?", AlertType.CONFIRMATION,
					ButtonType.OK, ButtonType.CANCEL) == ButtonType.OK)
				deleteProduct();
		}
		default -> System.out.println("No action assigned");
		}
	}
	
	/*
	 * private TableCell<Category, 
	 */
}
