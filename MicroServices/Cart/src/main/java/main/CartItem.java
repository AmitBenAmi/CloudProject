package main;

public class CartItem {

	public final static String Delimeter = ":";
	public static String createId(String username, String itemId) {
		return username + Delimeter + itemId;
	}

	// ===================================================================
	private final String _id;
	private final int quantity;
	private final String _rev;
	
	public CartItem(String username, String itemId, int quantity) {
		this._id = createId(username, itemId);
		this.quantity = quantity;
		this._rev = null;
	}
	
	// Copy ctor
	private CartItem(CartItem item, int quantity) {
		this._id = item._id;
		this.quantity = quantity;
		this._rev = item._rev;
	}
	
	@Override
	public String toString() {
		return String.format("id=%s, quantity=%s, rev=%s", this._id, this.quantity, this._rev);
	}
	
	public CartItem changeQuantity(int quantity) {
		return new CartItem(this, quantity);
	}
}
