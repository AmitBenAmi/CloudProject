package main;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class Main {

	public static void main(String[] args) {
		String password = "B4veLKbX6tQp";
		String username = "cloud.shop.project";
		
		GmailMailSSL gmail = new GmailMailSSL(username, password);
		
		GmailQueueSubscriber gmailSubscriber = new GmailQueueSubscriber(gmail);
		
		Queue q = new Queue(gmailSubscriber);
		q.listen();
	}
}
