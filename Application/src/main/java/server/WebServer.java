package server;

import spark.Spark;

public class WebServer {
	
	public static void main(String[] args) {
		Spark.port(80);
		
		Spark.staticFiles.location("/shop");
		
		Spark.redirect.get("/", "/index.html");
	}
} 
