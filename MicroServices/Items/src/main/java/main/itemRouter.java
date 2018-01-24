package main;
import java.util.List;

import com.google.gson.Gson;

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
	
	private String getItems(Request req, Response res) {
		List<Item> items = this.db.findAll(Item.class);
		return toJsonString(items);
	}
	
	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}
	
	public void init() {
		Spark.get("/items/:itemid", this::getItembyId);
		Spark.get("/items", this::getItems);
	}

}
