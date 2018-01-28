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
import main.itemRouter;


public class CheckoutQueueSubscriber implements QueueSubscriber {

	private final DBClient db;
	private final itemRouter itemRouter;
	private final JsonParser parser;
	
	public CheckoutQueueSubscriber(DBClient db, itemRouter itemrouter) {
		this.db = db;
		this.parser = new JsonParser();
		this.itemRouter = itemrouter;
	}
	
	@Override
	public void getMessage(String message) {
		JsonObject jsonMessage = parser.parse(message).getAsJsonObject();
		
		try {
			JsonArray itemsArr = jsonMessage.get("items").getAsJsonArray();
			itemsArr.forEach(item -> {
				String cartItemId = item.getAsJsonObject().get("_id").getAsString().split(":")[1];
				int quantity = item.getAsJsonObject().get("quantity").getAsInt();
				
				itemRouter.addNumberItemRedis(cartItemId, quantity);
			});
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
