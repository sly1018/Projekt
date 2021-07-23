package controller;

import java.util.List;

import common.MessageBox;
import exception.CategoryRepositoryException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.Category;
import repository.CategoryDbRepository;
import repository.CategoryRepository;

public class CategoryTableController {

	private CategoryRepository repository;

	@FXML
	private Button btnAddCategory;

	@FXML
	private Button btnEditCategory;

	@FXML
	private Button btnDeleteCategory;

	@FXML
	private Button btnReloadCategory;

	@FXML
	private TableView<Category> tblCategories;

	@FXML
	private TableColumn<Category, Integer> colId;

	@FXML
	private TableColumn<Category, String> colName;

	// for the binding
	private ListProperty<Category> categories;

	// binding of the selected product to an ObjectProperty
	private ObjectProperty<Category> selectedCategory;

	public CategoryTableController() {
		// creating the properties for the binding
		categories = new SimpleListProperty<>();
		selectedCategory = new SimpleObjectProperty<>();
	}

	// getter
	public Category getSelectedCategory() {
		return selectedCategory.get();
	}

	// setter
	public void setSelectedCategory(Category selCategory) {
		selectedCategory.set(selCategory);
	}

	public ObjectProperty<Category> selectedCategoryProperty() {
		return selectedCategory;
	}

	// initializing the repository
	public void setConnection(String dbUrl, String username, String password) {
		// forwarding the connection-information to the DB-repository
		repository = new CategoryDbRepository(dbUrl, username, password);
		reload();
	}

	@FXML
	private void initialize() {
		System.out.println("Category List initialize");

		// handler for changes in the selection view in ListView
		tblCategories.getSelectionModel().selectedItemProperty().addListener(
				// setting the ObjectProperty as Category-Object
				(o, oldCat, newCat) -> {
					// setting the Category-Object in Object-Property
					// trigger a changed-event from the property
					selectedCategory.set(newCat);
				});

		// binding of the disable-properties
		// buttons will be disabled when no category is selected
		btnEditCategory.disableProperty().bind(Bindings.isNull(selectedCategory));
		btnDeleteCategory.disableProperty().bind(Bindings.isNull(selectedCategory));
		// setting the visibility property
		// Box will be shown when a category is selected

		// cell-value-factories for the columns
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// the items in the table will be updated automatically when ObservableList in
		// the property changes
		tblCategories.itemsProperty().bind(categories);

	}

	@FXML
	public void addCategory() {
		try {
			// showing the dialog without the object
			EditCategoryWindow dlg = new EditCategoryWindow();
			Category entity = dlg.showModal();
			if (entity != null) {
				System.out.println("New Category: " + entity);
				repository.insertCategory(entity);
				reload();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("New Category", "Error occured" + e.getMessage(), AlertType.ERROR, ButtonType.OK);
		}
	}

	@FXML
	public void editCategory() {
		try {
			// new local variable
			Category selCategory = getSelectedCategory();
			// editing the selected category
			// showing the dialog with the selected category
			EditCategoryWindow dlg = new EditCategoryWindow(selCategory);
			Category entity = dlg.showModal();
			if (entity != null) {
				repository.updateCategory(entity);
				// get the index of the original-object
				int index = categories.indexOf(getSelectedCategory());
				// setting the changed object into the index
				categories.set(index, entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("Edit category\n", "Error occured during recording" + e.getMessage(), AlertType.ERROR,
					ButtonType.OK);
		}
	}

	@FXML
	public void deleteCategory() {
		try {
			System.out.println("Delete category: " + getSelectedCategory());

			// delete in the repository
			repository.deleteCategory(getSelectedCategory().getId());
			reload();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("Delete category", "Error occurred during deleting\n" + e.getMessage(), AlertType.ERROR,
					ButtonType.OK);
		}
	}

	@FXML
	public void reload() {
		try {
			System.out.println("Reloading...");
			this.categories.set(FXCollections.observableArrayList(repository.selectAll()));
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("Reload", "Error occured during loading" + e.getMessage(), AlertType.ERROR, ButtonType.OK);
		}
	}

	@FXML
	public void onClicked(MouseEvent me) {
		System.out.println("Mouse clicked");

		// when the mouse is clicked twice
		if (me.getClickCount() == 2) {
			editCategory();
		}
	}

	@FXML
	public void onKey(KeyEvent ke) {
		System.out.printf("Key pressed KeyCode=%s\n", ke.getCode());

		switch (ke.getCode()) {
		case ENTER -> editCategory();
		case DELETE -> {
			if (MessageBox.show("Delete the category", "Delete the selected dataset?", AlertType.CONFIRMATION,
					ButtonType.OK, ButtonType.CANCEL) == ButtonType.OK)
				deleteCategory();
		}
		default -> System.out.println("No action assigned");
		}
	}

	public List<Category> getAllCategories() {
		try {
			List<Category> retVal = repository.selectAll();
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show("Edit product", "Error occured during recording" + e.getMessage(), AlertType.ERROR,
					ButtonType.OK);
			return null;
		}
	}

	public Category getCategoryById(Integer id) throws CategoryRepositoryException {
		return repository.selectById(id);
	}

	public Category getCategoryByName(String n) throws CategoryRepositoryException {
		return repository.selectByName(n);
	}

}
