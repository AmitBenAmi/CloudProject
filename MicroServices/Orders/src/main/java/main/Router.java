package main;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;
import spark.http.matching.Halt;

public abstract class Router {

	private final DBClient db;
	private final JWTToken jwtTokener;
	
	public Router(DBClient db) throws IllegalArgumentException, UnsupportedEncodingException {
		this.db = db;
		this.jwtTokener = new JWTToken();
	}
	
	private JsonObject toJson(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}
	
	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}
	
	private boolean verifyJWTUsername(String token, String username)
	{
		DecodedJWT jwt = this.jwtTokener.validate(token);
		return jwt.getClaim("username").equals(username);
	}
	
	public abstract void init();
}
