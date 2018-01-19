package com.cloudproject.users.couchdb;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

public class Client implements AutoCloseable {
	
	private final CouchDbClient couchdb;
	
	public Client() {
		couchdb = new CouchDbClient("com/cloudproject/users/config/couchdb.properties");
	}
	
	public <T> T find(String id, Class<T> type) {
		return couchdb.find(type, id);
	}
	
	public void save(Object obj) throws RuntimeException {
		Response r = couchdb.save(obj);
		
		if (r.getError() != null) {
			throw new RuntimeException(responseToExceptionMsg(r));
		}
	}

	@Override
	public void close() throws Exception {
		couchdb.shutdown();
	}
	
	private String responseToExceptionMsg(Response response) {
		return String.format("status=%s, error=%s, reason=%s", response.getId(), response.getError(), response.getReason());
	}
}