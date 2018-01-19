package main;

import spark.Spark;

public class WebServer {

	public static void main(String[] args) {
		DBClient db = new DBClient();
		
		Spark.port(80);
		new itemRouter(db).init();
	}

}
