package main;

import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;

public class Router {

	private final DBClient db;
	
	public Router(DBClient db) {
		this.db = db;
	}
	
	private String saveItem(Request req, Response res) {
		//TODO: Add jwt
		// Get params
		JsonObject requestJson = toJson(req.body());
		String username = requestJson.get("username").getAsString();
		String itemid = requestJson.get("itemid").getAsString();
		int quantity = requestJson.get("quantity").getAsInt();
		
		// Check if item exists
		Optional<CartItem> itemOpt = db.findOpt(CartItem.createId(username, itemid), CartItem.class);
		CartItem item;
		if (itemOpt.isPresent()) {
			item = itemOpt.get().changeQuantity(quantity);
		} else {
			item = new CartItem(username, itemid, quantity);
		}
		
		db.save(item);
		return "";
	}
	
	private String removeItem(Request req, Response res) {
		// TODO: Add jwt
		
		// Get params
		JsonObject requestJson = toJson(req.body());
		String username = requestJson.get("username").getAsString();
		String itemid = requestJson.get("itemid").getAsString();
		
		// Find item in order to get rev and remove
		Optional<CartItem> itemOpt = db.findOpt(CartItem.createId(username, itemid), CartItem.class);
		itemOpt.ifPresent(item -> {
			db.remove(item);
		});
		
		return "";
	}
	
	private String getItemsOfUser(Request req, Response res) {
		// TODO: Add jwt
		
		// Get params
		String username = req.params("username");
		
		// Create query
		JsonObject regex = new JsonObject();
		regex.addProperty("$regex", "^" + username + CartItem.Delimeter);
		JsonObject idQuery = new JsonObject();
		idQuery.add("_id", regex);
		JsonObject selector = new JsonObject();
		selector.add("selector", idQuery);
		
		List<CartItem> found = db.findBySelector(selector, CartItem.class);
		return toJsonString(found);
	}
	
	private JsonObject toJson(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}
	
	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}
	
	public void init() {
		Spark.post("/item", this::saveItem);
		Spark.delete("/item", this::removeItem);
		Spark.get("/items/:username", this::getItemsOfUser);
	}
}
