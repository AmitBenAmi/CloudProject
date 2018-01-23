package main;

import java.io.UnsupportedEncodingException;

import spark.Spark;

public class WebServer {

	public static void main(String[] args) throws IllegalArgumentException, UnsupportedEncodingException {
		DBClient db = new DBClient();
		
		Spark.port(8084);
		new Router(db).init();
	}

}
