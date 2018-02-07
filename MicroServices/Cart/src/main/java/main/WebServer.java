package main;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

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
		
		Spark.port(ObjectUtils.firstNonNull(getNumericEnvVariable("cartPort"), 8082));
		allowCORS();
		new Router(db, queue).init();
	}
	
	private static void allowCORS() {
		// Answer method & headers allowed in options request
		Spark.options("/*", (request, response) -> {
			response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			response.header("Access-Control-Allow-Headers", request.headers("Access-Control-Request-Headers"));
			return "";
        });
		
		String origin = getStringEnvVariable("origin");
		
		// Add for each request allowed origin
		Spark.after("/*", (req, res) -> {
			res.header("Access-Control-Allow-Origin", ObjectUtils.firstNonNull(origin, req.headers("Origin")));
			res.header("Access-Control-Allow-Credentials", "true");
		});
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
