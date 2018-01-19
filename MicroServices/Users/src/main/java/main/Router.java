package main;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;

public class Router {

	private final DBClient db;
	
	public Router(DBClient db) {
		this.db = db;
	}
	
	private String checkUser(Request req, Response res) throws IllegalArgumentException, UnsupportedEncodingException {
		JsonObject requestJson = toJson(req.body());
		String username = requestJson.get("username").getAsString();
		String password = requestJson.get("password").getAsString();
		
		List<User> users = this.db.findByProp("userName", username, User.class);
		if (users.size() == 1) {
			User user = users.get(0);
			if (user.passwordValid(password)) {
				String token = new JWTToken().create(user);
				res.cookie("jwt", token);
				return token;
			}
		}
		
		// If not returned then not authorized
		res.status(401);
		return "invalid username/password";
	}
	
	private String addUser(Request req, Response res)
	{
		JsonObject requestJson = toJson(req.body());
		String username = requestJson.get("username").getAsString();
		String password = requestJson.get("password").getAsString();
		String email = requestJson.get("email").getAsString();

		User user = new User(username, MD5.hashPassword(password), email);
		// Will throw exception if user id exists
		this.db.save(user);
		
		return "";
	}
	
	private JsonObject toJson(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}
	
	public void init() {
		Spark.post("/checkuser", this::checkUser);
		Spark.post("/adduser", this::addUser);
	}
} 
