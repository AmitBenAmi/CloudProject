package server;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import spark.Filter;
import spark.Redirect;
import spark.Request;
import spark.Response;
import spark.Spark;

public class MicroServicesFilter implements Filter {

	private final Map<String, String> apiMap;
	
	public MicroServicesFilter(Map<String, String> apiMap) {
		this.apiMap = apiMap;
		// Make sure all urls & prefixes doesn't end with /
		this.clearLastSlash();
	}
	
	@Override
	public void handle(Request request, Response response) throws Exception {
		String url = request.uri();
		
		Optional<String> matchPrefix = apiMap.keySet().stream()
											 		  .filter(prefix -> isUrlPrefixed(url, prefix))
											 		  .findFirst();
		
		matchPrefix.ifPresent(prefix -> {
			String newUrl = this.apiMap.get(prefix) + UrlWithoutPrefix(url, prefix);
			
			if (request.requestMethod().equals("GET")) {
				response.redirect(newUrl);
			}
			else {
				response.redirect(newUrl, 307);
			}
			
			Spark.halt();
		});
	}

	private boolean isUrlPrefixed(String url, String prefix) {
		return url.startsWith(prefix + "/") || url.equals(prefix); 
	}
	
	private String UrlWithoutPrefix(String url, String prefix) {
		return url.substring(prefix.length(), url.length());
	}
	
	private void clearLastSlash() {
		Set<String> apiPrefixes = apiMap.keySet();
		apiPrefixes.forEach(key -> {
			if (key.endsWith("/")) {
				this.apiMap.put(removeLastCharFrom(key), apiMap.get(key));
				this.apiMap.remove(key);
			}
		});
		
		apiPrefixes = apiMap.keySet();
		apiPrefixes.forEach(key -> {
			String url = apiMap.get(key);
			if (url.endsWith("/")) {
				apiMap.put(key, removeLastCharFrom(url));
			}
		});
		
	}
	private String removeLastCharFrom(String s) {
		return s.substring(0, s.length() -1);
	}
}
