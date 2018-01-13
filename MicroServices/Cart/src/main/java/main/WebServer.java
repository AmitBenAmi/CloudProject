package main;

import java.util.HashMap;
import java.util.Map;

import spark.Spark;

public class WebServer {

	public static void main(String[] args) {
		DBClient db = new DBClient();
		
		Map m = new HashMap<String, String>();
		m.put("sss", "sss");
		m.put("user", "blabla");
		
		db.save(m);
		
	}
}
