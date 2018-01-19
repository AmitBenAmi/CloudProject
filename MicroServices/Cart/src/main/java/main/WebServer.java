package main;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;

public class WebServer {

	public static void main(String[] args) throws IllegalArgumentException, UnsupportedEncodingException {
		DBClient db = new DBClient();
		Queue queue = new Queue();
		
		Spark.port(8082);
		new Router(db, queue).init();
	}
}
