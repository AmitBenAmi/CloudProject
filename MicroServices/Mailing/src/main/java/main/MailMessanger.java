package main;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface MailMessanger {
	void sendMail(String toEmail, String subject, String htmlContent) throws AddressException, MessagingException;
}
