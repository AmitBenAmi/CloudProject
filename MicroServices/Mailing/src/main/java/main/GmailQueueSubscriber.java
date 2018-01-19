package main;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class GmailQueueSubscriber implements QueueSubscriber {
	private MailMessanger messanger;
	
	public GmailQueueSubscriber(MailMessanger messanger) {
		this.messanger = messanger;
	}
	
	public void getMessage(String message) {
		String mailAddress = message;
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

}
