package com.cloudproject.items.models;

import org.json.simple.JSONObject;

public class DbConfig {
	
	private String url;
	private String name;
	private String username;
	private String password;
	
	
	public DbConfig(JSONObject object){
		 this.url = (String) object.get("url");
		 this.url = (String) object.get("name");
		 this.url = (String) object.get("username");
		 this.url = (String) object.get("password");
	}
	
}
