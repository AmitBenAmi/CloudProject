package main;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;
import spark.http.matching.Halt;

public class Router {

	private final DBClient db;
	private final Queue queue;
	private final JWTToken jwtTokener;

	public Router(DBClient db, Queue queue) throws IllegalArgumentException, UnsupportedEncodingException {
		this.db = db;
		this.queue = queue;
		this.jwtTokener = new JWTToken();
	}

	private String saveItem(Request req, Response res) {
		// Get params
		JsonObject requestJson = toJson(req.body());
		String jwt = requestJson.get("jwt").getAsString();
		String username = requestJson.get("username").getAsString();
		String itemid = requestJson.get("itemid").getAsString();
		int quantity = requestJson.get("quantity").getAsInt();

		// Check identity
		if (!verifyJWTUsername(jwt, username)) {
			// Immidatly exists the function and return 401
			Spark.halt(401, String.format("Username %s is not authorized", username));
		}

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
		// Get params
		JsonObject requestJson = toJson(req.body());
		String jwt = requestJson.get("jwt").getAsString();
		String username = requestJson.get("username").getAsString();
		String itemid = requestJson.get("itemid").getAsString();

		// Check identity
		if (!verifyJWTUsername(jwt, username)) {
			// Immidatly exists the function and return 401
			Spark.halt(401, String.format("Username %s is not authorized", username));
		}

		// Find item in order to get rev and remove
		Optional<CartItem> itemOpt = db.findOpt(CartItem.createId(username, itemid), CartItem.class);
		itemOpt.ifPresent(item -> {
			db.remove(item);
		});

		return "";
	}

	private String getItemsOfUser(Request req, Response res) {
		// Get params
		JsonObject requestJson = toJson(req.body());
		String jwt = requestJson.get("jwt").getAsString();
		String username = requestJson.get("username").getAsString();

		// Check identity
		if (!verifyJWTUsername(jwt, username)) {
			// Immidatly exists the function and return 401
			Spark.halt(401, String.format("Username %s is not authorized", username));
		}

		return toJsonString(getItemsInCart(username));
	}

	private String checkout(Request req, Response res) {
		// Get params
		JsonObject requestJson = toJson(req.body());
		String jwt = requestJson.get("jwt").getAsString();
		String username = requestJson.get("username").getAsString();

		// Check identity
		if (!verifyJWTUsername(jwt, username)) {
			// Immidatly exists the function and return 401
			Spark.halt(401, String.format("Username %s is not authorized", username));
		}
		
		List<CartItem> items = getItemsInCart(username);
		
		JsonObject json = new JsonObject();
		json.addProperty("username", username);
		json.add("items", new Gson().toJsonTree(items));
		
		queue.sendMessage(toJsonString(json));
		
		items.forEach(item -> {
			db.remove(item);
		});
		
		return "";
	}
	
	private List<CartItem> getItemsInCart(String username) {
		// Create query
		JsonObject regex = new JsonObject();
		regex.addProperty("$regex", "^" + username + CartItem.Delimeter);
		JsonObject idQuery = new JsonObject();
		idQuery.add("_id", regex);
		JsonObject selector = new JsonObject();
		selector.add("selector", idQuery);

		return db.findBySelector(selector, CartItem.class);
	}

	private JsonObject toJson(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}

	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}

	private boolean verifyJWTUsername(String token, String username) {
		DecodedJWT jwt = this.jwtTokener.validate(token);
		return jwt.getClaim("username").equals(username);
	}

	public void init() {
		Spark.post("/item", this::saveItem);
		Spark.delete("/item", this::removeItem);
		Spark.get("/items", this::getItemsOfUser);
		Spark.post("/checkout", this::checkout);
	}
}
