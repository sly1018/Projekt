package model;

import java.time.LocalDate;

public class Product {

	private static int counter;

	private int id, quantity;
	private String name, description;
	private double price;
	private LocalDate entryDate;
	private int categoryId;

	static {
		counter = 1;
	}

	public Product() {
		this.id = counter++;
	}

	public Product(int q, String n, String d, double p, LocalDate ed, int t) {
		this.id = counter++;
		this.name = n;
		this.description = d;
		this.quantity = q;
		this.price = p;
		this.entryDate = ed;
		this.categoryId = t;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public LocalDate getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Product [id=");
		builder.append(getId());
		builder.append(", name=");
		builder.append(getName());
		builder.append(", quantity=");
		builder.append(getQuantity());
		builder.append(", description=");
		builder.append(getDescription());
		builder.append(", price=");
		builder.append(getPrice());
		builder.append(", entryDate=");
		builder.append(getEntryDate());
		builder.append(", categoryId=");
		builder.append(getCategoryId());
		builder.append("]");

		return builder.toString();
	}

}
