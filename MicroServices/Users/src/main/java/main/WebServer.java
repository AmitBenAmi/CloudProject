package main;

import java.io.UnsupportedEncodingException;

import spark.Spark;

public class WebServer {
	
	public static void main(String[] args) throws IllegalArgumentException, UnsupportedEncodingException {
		DBClient db = new DBClient();
		Spark.port(8084);
		
		allowCORS();

		new Router(db).init();

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
