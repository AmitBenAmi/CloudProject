package main;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
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
	
	public void addNumberItemRedis(String _id, int number){
		String key = "Count:" + _id;
		String member = Integer.toString(number);
		Jedis jedis = redisPool.getResource();
		try {
			//checking if the key already exists
			if(jedis.exists(key)) {
				//getting he members of the key
				String keyMember = jedis.get(key);
				int result = Integer.parseInt(keyMember);
				// sum the numbers
				member = Integer.toString(number + result);
				// changing the value 
				jedis.getSet(key, member);
			} else {
				//save new item to the redis
				jedis.set(key, member);
			}
	
		} catch (JedisException e){
			if (jedis != null)
			{
				redisPool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (jedis != null) {
				redisPool.returnResource(jedis);
			}
		}
	}

	public void init() {
		Spark.get("/items/:itemid", this::getItembyId);
		Spark.get("/items", this::getItems);
	}

}
