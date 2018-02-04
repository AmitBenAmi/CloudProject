package main;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang3.ObjectUtils;

public class Main {
	
	public static final String gatewayAddress = String.format("%s:%d", ObjectUtils.firstNonNull(getStringEnvVariable("gatewayUrl"), "http://localhost"), ObjectUtils.firstNonNull(getNumericEnvVariable("gatewayPort"), 8080));

	public static void main(String[] args) {
		String password = "B4veLKbX6tQp";
		String username = "cloud.shop.project";
		
		GmailMailSSL gmail = new GmailMailSSL(username, password);
		
		GmailQueueSubscriber gmailSubscriber = new GmailQueueSubscriber(gmail);
		
		Queue q = new Queue(gmailSubscriber);
		q.listen();
	}
	
	private static Integer getNumericEnvVariable(String envVarName) {
		Integer envVarValue = null;
		try {
			envVarValue = Integer.parseInt(System.getenv(envVarName));
		}
		catch (Exception e) {
			System.out.println(String.format("%s environment variable isn't defined", envVarName));
		}
		
		return envVarValue;
	}
	
	private static String getStringEnvVariable(String envVarName) {
		String envVarValue = null;
		try {
			envVarValue = System.getenv(envVarName).toString();
		}
		catch (Exception e) {
			System.out.println(String.format("%s environment variable isn't defined", envVarName));
		}
		
		return envVarValue;
	}
}
