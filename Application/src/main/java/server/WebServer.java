package server;

import java.util.HashMap;
import java.util.Map;

import spark.Spark;

public class WebServer {

	public static void main(String[] args) {
		Spark.port(8080);

		Spark.staticFiles.location("/shop");

		Spark.redirect.get("/", "/index.html");

		// Redirect to services
		Map<String, String> servicesRouting = new HashMap<String, String>() {
			{
				put("/api/items", "http://localhost:8083");
				put("/api/cart", "http://localhost:8082");
				put("/api/orders", "http://localhost:8081");
				put("/api/users", "http://localhost:8084");
			}
		};

		Spark.before(new MicroServicesFilter(servicesRouting));
	}
}
