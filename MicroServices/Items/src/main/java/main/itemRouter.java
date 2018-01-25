package main;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import spark.Request;
import spark.Response;
import spark.Spark;

public class itemRouter {
	
	private final DBClient db;
	private final JedisPool redisPool;
	
	public itemRouter(DBClient db, JedisPool pool) {
		this.db = db;
		this.redisPool = pool;
	}
	
	private String getItembyId(Request req, Response res) {
		String itemId = req.params("itemid");
		
		Optional<String> itemJsonOpt = getItemJsonFromCache(itemId);
		
		if (itemJsonOpt.isPresent()) {
			return itemJsonOpt.get();
		}
		else {
			Item item = this.db.find(itemId, Item.class);
			String itemJson = toJsonString(item);
			saveItemJsonToCache(itemId, itemJson);
			return itemJson;
		}
	}
	
	private String getItems(Request req, Response res) {
		List<Item> items = this.db.findAll(Item.class);
		return toJsonString(items);
	}
	
	private String toJsonString(Object object) {
		return new Gson().toJson(object);
	}
	
	private Optional<String> getItemJsonFromCache(String id) {
		try (Jedis redis = redisPool.getResource()) {
			String item = redis.get(id);
			return item == null ? Optional.empty() : Optional.of(item);
		}
		catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	private void saveItemJsonToCache(String id, String itemJson) {
		try (Jedis redis = redisPool.getResource()) {
			int numOfSecondToExpire = 3600;
			redis.setex(id, numOfSecondToExpire, itemJson);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		Spark.get("/items/:itemid", this::getItembyId);
		Spark.get("/items", this::getItems);
	}

}
