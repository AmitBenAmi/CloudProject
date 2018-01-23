package main;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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
		JsonObject emailMessage = this.getEmailOfUser(username);
		String mailAddress = this.getUserEmailFromMessage(emailMessage);
		
		try {
			this.messanger.sendMail(mailAddress, "Subject Text", "hey <br> <strong>hello from microservice!</strong>");
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
