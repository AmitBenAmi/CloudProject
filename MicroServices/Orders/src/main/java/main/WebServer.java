package main;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import queue.CheckoutQueueSubscriber;
import queue.Queue;
import spark.Request;
import spark.Response;
import spark.Spark;

public class WebServer {

	public static final String gatewayAddress = "http://localhost:8080";
	
	public static void main(String[] args) throws IllegalArgumentException, UnsupportedEncodingException {
		DBClient db = new DBClient();
		
		Spark.port(8081);
		allowCORS();
		new OrdersRouter(db).init();
		
		CheckoutQueueSubscriber subscriber = new CheckoutQueueSubscriber(db);
		Queue queue = new Queue(subscriber);
		queue.listen();
	}
	
	private static void allowCORS() {
		// Answer method & headers allowed in options request
		Spark.options("/*", (request, response) -> {
			response.header("Access-Control-Allow-Methods", "GET, POST, PUT");
			response.header("Access-Control-Allow-Headers", request.headers("Access-Control-Request-Headers"));
			return "";
        });
		
		// Add for each request allowed origin
		Spark.after("/*", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
		});
	}
}
