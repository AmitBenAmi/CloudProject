package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import spark.Request;
import spark.Response;
import spark.Spark;

public class itemRouter {
	
	private final DBClient db;
	private final JedisPool redisPool;
	private final JsonParser parser;
	
	public itemRouter(DBClient db, JedisPool pool) {
		this.db = db;
		this.redisPool = pool;
		this.parser = new JsonParser();
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
	
	private String getItemName(Request req, Response res) {
		String itemString = this.getItembyId(req, res);
		JsonObject itemJson;
		//String itemId = req.params("itemid");
		itemJson = parser.parse(itemString).getAsJsonObject();
		return itemJson.get("name").getAsString();
	}
	
	private JsonArray toJsonArray(String json) {
		return new JsonParser().parse(json).getAsJsonArray();
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

	public String mostOrderedItems(Request req, Response res){
		int number = Integer.parseInt(req.params("num"));
		
		List<JSONObject> listOfIDs = new ArrayList<JSONObject>();
		JSONObject JsonObject;
		Jedis jedis = redisPool.getResource();
		
		//taking all the keys that started with "Count:"
		Set<String> allKeys = jedis.keys("Count:*");
		for(String key : allKeys) {
			int num = Integer.parseInt(jedis.get(key));
			JsonObject = new JSONObject();
			JsonObject.put("key" , key.substring(6));
			JsonObject.put("count" , num);
			listOfIDs.add(JsonObject);
			}
		
		listOfIDs = sort(listOfIDs);
		
		if (listOfIDs.size() < number)
		{
			number = listOfIDs.size();
		}
		
		
		//taking only the first X items
		JsonArray ids = new JsonArray();
		for(int i = 0; i < number; i++) {
			ids.add(listOfIDs.get(i).getString("key"));
		}
		
		return getItems(ids, Item.class);
	}
	
	private <T> String getItems(JsonArray ids, Class<T> clazz) {
		JsonObject inList = new JsonObject();
		inList.add("$in", ids);
		JsonObject query = new JsonObject();
		query.add("_id", inList);
		JsonObject selector = new JsonObject();
		selector.add("selector", query);
		
		return toJsonString(db.findBySelector(selector, clazz));
	}
	
	private String getItemsByIds(Request req, Response res) {
		 JsonArray itemIds = toJsonArray(req.body());

		 return getItems(itemIds, Item.class);
	}
	
	private List<JSONObject> sort(List<JSONObject> listOfIDs){
		
		Collections.sort(listOfIDs, new Comparator<JSONObject>() {
		    @Override
		    public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
		        int compare = 0;
		        try
		        {
		            int keyA = jsonObjectA.getInt("count");
		            int keyB = jsonObjectB.getInt("count"); 
		            compare = Integer.compare(keyB, keyA);
		        }
		        catch(JSONException e)
		        {
		            e.printStackTrace();
		        }
		        return compare;
		    }
		});
		return listOfIDs;			
	}
	
	public void init() {
		Spark.get("/items/:itemid", this::getItembyId);
		Spark.get("/items", this::getItems);
		Spark.get("/items/mostorderditems/:num", this::mostOrderedItems);
		Spark.get("/items/name/:itemid", this::getItemName);
		Spark.post("/items", this::getItemsByIds);
	}

}
