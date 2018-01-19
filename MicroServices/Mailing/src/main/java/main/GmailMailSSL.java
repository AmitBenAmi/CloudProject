package main;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailMailSSL implements MailMessanger {
	
	private final Properties properties;
	private final String username;
	private final String password;
	
	public GmailMailSSL(String username, String password) {
		this.username = username;
		this.password = password;
		
		this.properties = new Properties();
		this.properties.put("mail.smtp.host", "smtp.gmail.com");
		this.properties.put("mail.smtp.starttls.enable", "true");
		this.properties.put("mail.smtp.auth", "true");
		this.properties.put("mail.smtp.port", "587");
	}
	
	public void sendMail(String toEmail, String subject, String htmlContent) throws AddressException, MessagingException {
		Session mailSession = Session.getInstance(this.properties);
		MimeMessage message = new MimeMessage(mailSession);
		
		// Prepare email
		message.setFrom(new InternetAddress(username + "@gmail.com"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		message.setSubject(subject);
		message.setContent(htmlContent, "text/html");
		
		Transport.send(message, username, password);
	}
}
