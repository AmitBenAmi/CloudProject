package com.cloudproject.users.dal;

import com.cloudproject.users.couchdb.Client;;

public class UserDAL implements AutoCloseable {
	
	private Client client;
	
	public UserDAL() {
		client = new Client();
	}
	
	public void getUser() {
		
	}

	@Override
	public void close() throws Exception {
		client.close();
	}
}