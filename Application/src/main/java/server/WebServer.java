package server;

import java.util.HashMap;
import java.util.Map;

import spark.Spark;

public class WebServer {
	
	public static void main(String[] args) {
		Spark.port(80);
		
		Spark.staticFiles.location("/shop");
		
		Spark.redirect.get("/", "/index.html");
		
		// Redirect to services
		Map<String, String> servicesRouting = new HashMap<String, String>() {{
			put("/api/items", "http://google.com");
		}};
		
		Spark.before(new MicroServicesFilter(servicesRouting));
	}
} 
