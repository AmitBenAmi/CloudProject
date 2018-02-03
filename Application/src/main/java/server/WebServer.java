package server;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import spark.Spark;
import org.apache.commons.lang3.ObjectUtils;

public class WebServer {

	public static void main(String[] args) throws IllegalArgumentException, UnsupportedEncodingException {
		Integer applicationPort = getNumericEnvVariable("portNumber");
		
		//Spark.port(applicationPort != null ? applicationPort : 8080);

		Spark.staticFiles.location("/shop");

		Spark.redirect.get("/", "/index.html");

		// Redirect to services
		Map<String, String> servicesRouting = new HashMap<String, String>() {
			{
				put("/api/items", String.format("%s:%d", ObjectUtils.firstNonNull(getStringEnvVariable("itemsUrl"), "http://localhost"), ObjectUtils.firstNonNull(getNumericEnvVariable("itemsPort"), 8083)));
				put("/api/cart", String.format("%s:%d", ObjectUtils.firstNonNull(getStringEnvVariable("cartUrl"), "http://localhost"), ObjectUtils.firstNonNull(getNumericEnvVariable("cartPort"), 8082)));
				put("/api/orders", String.format("%s:%d", ObjectUtils.firstNonNull(getStringEnvVariable("ordersUrl"), "http://localhost"), ObjectUtils.firstNonNull(getNumericEnvVariable("ordersPort"), 8081)));
				put("/api/users", String.format("%s:%d", ObjectUtils.firstNonNull(getStringEnvVariable("usersUrl"), "http://localhost"), ObjectUtils.firstNonNull(getNumericEnvVariable("usersPort"), 8084)));
			}
		};

		Spark.before(new MicroServicesFilter(servicesRouting));
		
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
