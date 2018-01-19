package main;

import spark.Spark;

public class WebServer {

	public static void main(String[] args) {
		DBClient db = new DBClient();
		
		Spark.port(8084);
		new Router(db).init();
	}

}
