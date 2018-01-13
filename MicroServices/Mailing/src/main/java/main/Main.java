package main;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class Main {

	public static void main(String[] args) {
		String password = "B4veLKbX6tQp";
		String username = "cloud.shop.project";
		
		GmailMailSSL gmail = new GmailMailSSL(username, password);
		
		try {
			gmail.sendMail("benamiamit0@gmail.com", "test mail", "hey <br> <strong>hello from microservice!</strong>");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
