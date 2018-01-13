package main;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import com.google.gson.JsonObject;

public class DBClient {

	private final CouchDbClient connection;
	
	public DBClient() {
		// Read properties from couchdb.properties
		this.connection = new CouchDbClient();
	}
	
	public JsonObject find(String id) {
		return this.connection.find(JsonObject.class, id);
	}
	
	public <T> T find(String id, Class<T> clazz) {
		return this.connection.find(clazz, id);
	}
	
	/**
	 * Throws runtime exception on failure
	 * @param obj to save. can be POJO / Map / JsonObject
	 */
	public void save(Object obj) {
		Response r = this.connection.save(obj);
		// If there is error
		 if (r.getError() != null) {
			 throw new RuntimeException(responseToExceptionMsg(r));
		 }
	}
	
	/**
	 * Throws runtime exception on failure
	 * @param obj to update. can be POJO / Map / JsonObject
	 */
	public void update(Object obj) {
		Response r = this.connection.update(obj);
		// If there is error
		 if (r.getError() != null) {
			 throw new RuntimeException(responseToExceptionMsg(r));
		 }
	}
	
	
	private String responseToExceptionMsg(Response response) {
		return String.format("status=%s, error=%s, reason=%s", response.getId(), response.getError(), response.getReason());
	}
}
