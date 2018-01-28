package server;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.auth0.jwt.interfaces.DecodedJWT;

import spark.Filter;
import spark.Redirect;
import spark.Request;
import spark.Response;
import spark.Spark;

public class MicroServicesFilter implements Filter {

	private final Map<String, String> apiMap;
	private final JWTToken jwtTokener;

	public MicroServicesFilter(Map<String, String> apiMap) throws IllegalArgumentException, UnsupportedEncodingException {
		this.apiMap = apiMap;
		this.jwtTokener = new JWTToken();
		// Make sure all urls & prefixes doesn't end with /
		this.clearLastSlash();
	}

	@Override
	public void handle(Request request, Response response) throws Exception {
		String url = request.uri();
		String jwt = request.cookie("jwt");

		if (url.endsWith("html")) {
			if (!url.endsWith("login.html") && !verifyJWTUsername(jwt, "tomer")) {
				response.redirect("login.html");
			}
		} else {
			Optional<String> matchPrefix = apiMap.keySet().stream().filter(prefix -> isUrlPrefixed(url, prefix))
					.findFirst();

			matchPrefix.ifPresent(prefix -> {
				String newUrl = this.apiMap.get(prefix) + UrlWithoutPrefix(url, prefix);

				if (request.requestMethod().equals("GET")) {
					response.redirect(newUrl);
				} else {
					response.redirect(newUrl, 307);
				}

				Spark.halt();
			});
		}
	}
	
	private boolean verifyJWTUsername(String token, String username) {
		if (token != null) {
			DecodedJWT jwt = this.jwtTokener.validate(token);
			return jwt.getClaim("username").asString().equals(username);			
		} else {
			return false;
		}
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
		return s.substring(0, s.length() - 1);
	}
}
