package main;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Spark;

public class Router {

	private final DBClient db;
	private final JWTToken jwtTokener;
	
	public Router(DBClient db) throws IllegalArgumentException, UnsupportedEncodingException {
		this.db = db;
		this.jwtTokener = new JWTToken();
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
	
	private String getUserEmail(Request req, Response res) {
		String username = req.params("username");
		String jwt = req.cookie("jwt");
		
//		// Check identity
//		if (!verifyJWTUsername(jwt, username)) {
//			// Immidatly exists the function and return 401
//			Spark.halt(401, String.format("Username %s is not authorized", username));
//		}
		
		List<User> users = this.db.findByProp("userName", username, User.class);
		
		if (users.size() == 1) {
			User requestedUser = users.get(0);
			JsonObject email = new JsonObject();
			email.addProperty("email", requestedUser.email());
			return this.toJsonString(email);
		}
		
		res.status(404);
		return "User not found";
	}
		
	private JsonObject toJson(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}
	
	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}
	
	private boolean verifyJWTUsername(String token, String username) {
		DecodedJWT jwt = this.jwtTokener.validate(token);
		return jwt.getClaim("username").asString().equals(username);
	}
	
	public void init() {
		Spark.post("/checkuser", this::checkUser);
		Spark.post("/adduser", this::addUser);
		Spark.get("/getuseremail/:username", this::getUserEmail);
	}
} 
