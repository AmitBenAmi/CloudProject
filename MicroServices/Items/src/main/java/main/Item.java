package main;

public class Item {
	
	private final String _id;	
	private final String name;
	private final double price;
	private final String currency;
	private final String image;
	private final String _rev;

	public Item(String _id, String _rev, String name, double price, String currency, String image) {
		this._id = _id;
		this.name = name;
		this.price = price;
		this.currency = currency;
		this.image = image;
		this._rev = _rev;
	}	
	
	public String getName() {
		return this.name;
	}

}
