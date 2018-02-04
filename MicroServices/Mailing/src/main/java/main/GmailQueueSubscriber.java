package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class GmailQueueSubscriber implements QueueSubscriber {
	private MailMessanger messanger;
	private final JsonParser parser;
	
	public GmailQueueSubscriber(MailMessanger messanger) {
		this.messanger = messanger;
		this.parser = new JsonParser();
	}
	
	public void getMessage(String message) {
		String username = this.getUserNameFromMessage(message);
		List<String> items = this.getItemsFromMessage(message);
		String theContent = this.setMailMessage(items);
		JsonObject emailMessage = this.getEmailOfUser(username);
		String mailAddress = this.getUserEmailFromMessage(emailMessage);
		String string = String.format("Thanks for your order!, %s <br> <strong> %s </strong>", username, theContent.replace("\n", "<br>"));
		
		try {		
			this.messanger.sendMail(mailAddress, "ORDER CONFIRMED", string);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getUserNameFromMessage(String message) {
		JsonObject jsonMessage = parser.parse(message).getAsJsonObject();
		return jsonMessage.get("username").getAsString();
	}
	
	private List<String> getItemsFromMessage(String message) {
		List<String> ListIDs = new ArrayList<String>();
		JsonObject jsonMessage = parser.parse(message).getAsJsonObject();
		JsonArray itemsArr = jsonMessage.get("items").getAsJsonArray();
		itemsArr.forEach(item -> {
			String ItemId = item.getAsJsonObject().get("_id").getAsString().split(":")[1];
			ListIDs.add(ItemId);	
		});
		return ListIDs;
	}
	
	private String setMailMessage(List<String> IDs) {
		String message = "Your order is confirmed and we'll let you know when it's on the way " + '\n' + "The order is: ";
		
		for (String id:IDs){
			try {
				String name = this.getItemByID(id);
				message = message + name + '\n';
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		};

		return message;
	}
	
	private String getItemByID(String ID) {
		try {
			String body = Unirest.get(Main.gatewayAddress + "/api/items/items/name/" + ID).asString().getBody();
			return body;
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getUserEmailFromMessage(JsonObject message) {
		return message.get("email").getAsString();
	}
	
	private JsonObject getEmailOfUser(String username) {
		try {
			String body = Unirest.get(Main.gatewayAddress + "/api/users/getuseremail/" + username).asString().getBody();
			return parser.parse(body).getAsJsonObject();
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}
	
//	private JsonObject authenticateToUsers() {
//		try {
//			String body = Unirest.post(Main.gatewayAddress + "/api/service/authenticate/" + "mailing_service");
//			return parser.parse(body).getAsJsonObject();
//		} catch (UnirestException e) {
//			throw new RuntimeException(e);
//		}
//	}
}
