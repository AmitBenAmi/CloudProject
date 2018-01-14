package main;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;

public class WebServer {

	public static void main(String[] args) {
		DBClient db = new DBClient();
		
		new Router(db).init();
	}
}
