package main;

public class Item {
	
	private final String _id;	
	private final String name;
	private final double price;
	private final String currency;
	private final String _rev;

	public Item(String id, String name, double price, String currency) {
		this._id = id;
		this.name = name;
		this.price = price;
		this.currency = currency;
		this._rev = null;
	}	

}
