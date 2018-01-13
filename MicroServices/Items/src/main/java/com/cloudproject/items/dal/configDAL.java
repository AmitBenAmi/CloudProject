package com.cloudproject.items.dal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.cloudproject.items.models.DbConfig;

public class configDAL {
	final String CONFIG_FILE = "/itemsService/src/main/resources/com/cloudproject/items/configs/dbConfig.json";
	
	public DbConfig configDAL() {
		JSONParser parser = new JSONParser();
		DbConfig config = null;
				
		try {
			FileReader reader = new FileReader(CONFIG_FILE);
			Object obj = parser.parse(reader);
			JSONObject jsonObj = (JSONObject) parser.parse(reader);
			
			String url = (String) jsonObj.get("url");
			
			config = new DbConfig(jsonObj);
	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return config;
		  

	}
}
