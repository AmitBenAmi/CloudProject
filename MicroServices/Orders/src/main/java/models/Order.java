package models;

import java.util.List;

public class Order {
	
	private final String _id;
	private final String _rev;
	private final List<OrderItem> items;
	private final String userId;
	
	public Order(String id, List<OrderItem> items, String userId, String rev) {
		this._id = id;
		this.items = items;
		this.userId = userId;
		this._rev = rev;
	}
	
	public Order(String id, List<OrderItem> items, String userId) {
		this(id, items, userId, null);
	}
}
