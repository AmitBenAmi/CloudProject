package main;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;

public class itemRouter {
	
private final DBClient db;
	
	public itemRouter(DBClient db) {
		this.db = db;
	}
	
	private String getItembyId(Request req, Response res) {
		String itemId = req.params("itemid");
		Item item = this.db.find(itemId, Item.class);
		
		return toJsonString(item);
	}
	
	private List<Item> getItems(Request req, Response res) {
		JsonObject regex = new JsonObject();
		regex.addProperty("$eq", "_all_docs");
		JsonObject idQuery = new JsonObject();
		idQuery.add("_id", regex);
		JsonObject selector = new JsonObject();
		selector.add("selector", idQuery);
		
		return this.db.findBySelector(selector, Item.class);
	}
	
	private JsonObject toJson(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}
	
	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}
	
	public void init() {
		Spark.get("/items/:itemid", this::getItembyId);
		Spark.get("/items", this::getItems);
	}

}
