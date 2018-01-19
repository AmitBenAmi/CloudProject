package main;
import java.io.UnsupportedEncodingException;
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
	
	private String getItems(Request req, Response res) {
		String itemId = req.params("itemid");
		Item item = this.db.find(itemId, Item.class);
		
		return toJsonString(item);
	}
	
	private JsonObject toJson(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}
	
	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}
	
	public void init() {
		Spark.get("/items/:itemid", this::getItems);
	}

}
