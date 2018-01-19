package queue;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import main.DBClient;
import main.WebServer;
import models.Order;
import models.OrderItem;

public class CheckoutQueueSubscriber implements QueueSubscriber {

	private final DBClient db;
	private final JsonParser parser;
	
	public CheckoutQueueSubscriber(DBClient db) {
		this.db = db;
		this.parser = new JsonParser();
	}
	
	@Override
	public void getMessage(String message) {
		JsonObject jsonMessage = parser.parse(message).getAsJsonObject();
		
		try {
			Order newOrder = createOrderFromMessage(jsonMessage);
			db.save(newOrder);
		}
		catch(Exception e) {
			// TODO: write to queue of failed orders for more handling...
		}
	}
	
	private Order createOrderFromMessage(JsonObject message) {
		String username = message.get("username").getAsString();
		JsonArray itemsArr = message.get("items").getAsJsonArray();
		List<OrderItem> items = new ArrayList<>();
		itemsArr.forEach(item -> {
			
			String cartItemId = item.getAsJsonObject().get("_id").getAsString().split(":")[1];
			int quantity = item.getAsJsonObject().get("quantity").getAsInt();
			
			// Get the other details from the item service
			JsonObject fullItem = getDetailsOfItem(cartItemId);
			String title = fullItem.get("name").getAsString();
			double price = fullItem.get("price").getAsDouble();
			// TODO: go to items service to get the description, title, & price
			items.add(new OrderItem(cartItemId, title, price, quantity));
		});
		
		return new Order(items, username);
	}

	private JsonObject getDetailsOfItem(String id) {
		try {
			String body = Unirest.get(WebServer.gatewayAddress + "/api/items/item/" + id).asString().getBody();
			return parser.parse(body).getAsJsonObject();
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}
}
