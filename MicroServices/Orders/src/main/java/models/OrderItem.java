package models;

public class OrderItem {

	private final String id;
	private final String title;
	private final double price;
	private final int quantity;
	
	public OrderItem(String id, String title, double price, int quantity) {
		this.id = id;
		this.title = title;
		this.price = price;
		this.quantity = quantity;
	}
}
