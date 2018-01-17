package main;

import spark.Spark;

public class WebServer {

	public static void main(String[] args) {
		DBClient db = new DBClient();
		
		new Router(db).init();
		Spark.port(80);
	}

}
