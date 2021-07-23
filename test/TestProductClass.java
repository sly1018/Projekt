package test;

import java.time.LocalDate;

import model.Category;
import model.Product;

public class TestProductClass {

	public static void main(String[] args) {
		
		Product p = new Product();
		Category c =  new Category();
		c.setId(0);
		c.setName("Food");
		
		p.setDescription("A very famous book.");
		p.setEntryDate(LocalDate.of(2021, 06, 17));
		p.setName("The Master and Margarita");
		p.setPrice(9.99);
		p.setQuantity(10);
		
		
		System.out.println(p.toString());
		
	}

}
