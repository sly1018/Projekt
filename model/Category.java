package model;

public class Category {

	private int id;
	private String name;

	public Category() {
	}

	public Category(String n) {
		this.name = n;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Category [id= ");
		builder.append(getId());
		builder.append( ", name = ");
		builder.append(getName());
		builder.append("]");
		
		return builder.toString();
	}

}
