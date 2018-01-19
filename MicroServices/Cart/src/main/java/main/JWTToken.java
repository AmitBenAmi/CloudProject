package main;

import java.io.UnsupportedEncodingException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTToken {

	private final Algorithm algorithm;
	private final String issuer = "cloud-shop";
	
	public JWTToken() throws IllegalArgumentException, UnsupportedEncodingException {
		this.algorithm = Algorithm.HMAC256("WAkW3nZaYAUS-4fGmqJFN@Q|*Sf9");
	}
	
	public DecodedJWT validate(String token) {
	    JWTVerifier verifier = JWT.require(this.algorithm)
	        .withIssuer(issuer)
	        .build(); //Reusable verifier instance
	    return verifier.verify(token);
	}
} 
