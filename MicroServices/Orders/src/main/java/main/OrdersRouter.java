package main;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.gson.JsonObject;

import models.Order;
import spark.Request;
import spark.Response;
import spark.Spark;

public class OrdersRouter extends Router {

	private final DBClient db;
	
	public OrdersRouter(DBClient db) throws IllegalArgumentException, UnsupportedEncodingException {
		super();
		this.db = db;
	}

	private String getOrdersOfUser(Request req, Response res) {
		String token = req.cookie("jwt");
		String username = req.params("username");
		// Validate
		if (!this.verifyJWTUsername(token, username)) {
			Spark.halt(401, "unauthorized for user " + username);
		}
		
		// Create query
		JsonObject equal = new JsonObject();
		equal.addProperty("$eq", username);
		JsonObject userIdQuery = new JsonObject();
		userIdQuery.add("userId", equal);
		JsonObject selector = new JsonObject();
		selector.add("selector", userIdQuery);
		
		List<Order> orders = db.findBySelector(selector, Order.class);
		return toJsonString(orders);
	}
	
	@Override
	public void init() {
		Spark.get("/orders/:username", this::getOrdersOfUser);
	}
}
