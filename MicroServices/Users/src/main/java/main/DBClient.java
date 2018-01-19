package main;

import java.util.List;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;

import com.google.gson.JsonObject;

public class DBClient {

	private final CouchDbClient connection;
	
	public DBClient() {
		// Read properties from couchdb.properties
		this.connection = new CouchDbClient();
	}
	
	/**
	 * Throws NoDocumentException if document doesn't exists
	 * @param id
	 * @return
	 */
	public JsonObject find(String id) {
		return find(id, JsonObject.class);
	}
	
	public <T> List<T> findByProp(String property, String value, Class<T> cls) {
		JsonObject equal = new JsonObject();
		equal.addProperty("$eq", value);
		JsonObject query = new JsonObject();
		query.add(property, equal);
		JsonObject selector = new JsonObject();
		selector.add("selector", query);
		
		
		return this.connection.findDocs(selector.toString(), cls);
	}
	
	/**
	 * Throws NoDocumentException if document doesn't exists
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <T> T find(String id, Class<T> clazz) {
		return this.connection.find(clazz, id);
	}
	
	/**
	 * If document doesn't exists doesn't throw exception
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <T> Optional<T> findOpt(String id, Class<T> clazz) {
		try {
			return Optional.of(find(id, clazz));
		} catch (NoDocumentException ex) {
			return Optional.empty();
		}
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
	 * <b>Must contain latest _rev to update</b><br>
	 * Throws runtime exception on failure
	 * @param obj to update. can be POJO / Map / JsonObject must include _rev
	 */
	public void update(Object obj) {
		Response r = this.connection.update(obj);
		// If there is error
		 if (r.getError() != null) {
			 throw new RuntimeException(responseToExceptionMsg(r));
		 }
	}
	
	/**
	 * <b>Must contain latest _rev to remove</b><br>
	 * Throws runtime exception on failure
	 * @param obj to update. can be POJO / Map / JsonObject must include _rev
	 */
	public void remove(Object obj) {
		Response r = this.connection.remove(obj);
		// If there is error
		 if (r.getError() != null) {
			 throw new RuntimeException(responseToExceptionMsg(r));
		 }
	}
	
	public <T> List<T> findBySelector(JsonObject selector, Class<T> clazz) {
		return this.connection.findDocs(selector.toString(), clazz);
	}
	
	private String responseToExceptionMsg(Response response) {
		return String.format("status=%s, error=%s, reason=%s", response.getId(), response.getError(), response.getReason());
	}
}
