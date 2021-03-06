package controller;

import common.MessageBox;
import exception.CategoryRepositoryException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.Product;
import repository.CategoryDbRepository;
import repository.CategoryRepository;
import repository.ProductDbRepository;
import repository.ProductRepository;

public class ProductTableController {

	private ProductRepository repositoryProduct;

	private CategoryRepository repositoryCategory;

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

	@FXML
	private TableColumn<Product, String> colCat;

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
		repositoryProduct = new ProductDbRepository(dbUrl, username, password);
		repositoryCategory = new CategoryDbRepository(dbUrl, username, password);
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

		// lambda expression, interchangeable with anonymous class when interface is a functional class
		colCat.setCellValueFactory(cellData ->  new SimpleStringProperty(getNameOfCategory(cellData.getValue().getCategoryId())));

		// anonymous class
		colCat.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Product, String> param) {
						System.out.println(getNameOfCategory(param.getValue().getCategoryId()));
						String retVal = getNameOfCategory(param.getValue().getCategoryId());
						return new SimpleStringProperty(retVal);
					}
				});

		// the items in the table will be updated automatically when ObservableList in
		// the property changes
		tblProducts.itemsProperty().bind(products);
		
		// TODO: sort comments, from German to English, lambda und anonyme klasse austauschbar nur wenn der interface ein funktionales interface ist

	}

	@FXML
	public void addProduct() {
		try {// showing the dialog without the object
			EditProductWindow dlg = new EditProductWindow();
			Product entity = dlg.showModal();
			if (entity != null) {
				System.out.println("New Product: " + entity);
				repositoryProduct.insertProduct(entity);
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

				repositoryProduct.updateProduct(entity);

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
			repositoryProduct.deleteProduct(getSelectedProduct().getId());
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

			this.products.set(FXCollections.observableArrayList(repositoryProduct.selectAll()));
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

	private TableCell<Product, String> createCategoryNameCell(TableColumn<Product, String> col) {
		return new TableCell<Product, String>() {
			protected void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);
				if (empty || value == null) {
					setText("");
				} else {
					setText(value);
				}
			};
		};

	}

	private String getNameOfCategory(int id) {

		String retVal = null;

		try {
			retVal = repositoryCategory.selectById(id).getName();
		} catch (CategoryRepositoryException e) {
			e.printStackTrace();
			MessageBox.show("Problem with Category", "Category name could not be loaded", AlertType.CONFIRMATION,
					ButtonType.OK);
		}
		return retVal;
	}

}
