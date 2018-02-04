package main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.ObjectUtils;

import queue.CheckoutQueueSubscriber;
import queue.Queue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;
import spark.Spark;

public class WebServer {

	private static final String REDIS_CONNECTION_STRING = "rediss://admin:ZXAANHJXVGKUUWVT@portal310-7.bmix-lon-yp-2b6a8387-561b-4e60-8dce-33275f5fec2b.benamiamit0-gmail-com.composedb.com:22815";
	private static JedisPool pool;
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
		DBClient db = new DBClient();
		pool = new JedisPool(REDIS_CONNECTION_STRING, createDumbSSLSocketFactory(), null, null);
		
		Spark.port(ObjectUtils.firstNonNull(getNumericEnvVariable("itemsPort"),  8083));
		allowCORS();
		itemRouter itemRouter = new itemRouter(db, pool);
		itemRouter.init();
		
		CheckoutQueueSubscriber subscriber = new CheckoutQueueSubscriber(db, itemRouter);
		Queue queue = new Queue(subscriber);
		queue.listen();
	}
	
	private static void allowCORS() {
		// Answer method & headers allowed in options request
		Spark.options("/*", (request, response) -> {
			response.header("Access-Control-Allow-Methods", "GET, POST, PUT");
			response.header("Access-Control-Allow-Headers", request.headers("Access-Control-Request-Headers"));
			return "";
        });
		
		// Add for each request allowed origin
		Spark.after("/*", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://" + req.host());
			res.header("Access-Control-Allow-Credentials", "true");
		});
	}
	
	/**
	 * Create dumb ssl socket factory which accepts any certificate without any validation
	 */
	private static SSLSocketFactory createDumbSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
		TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// Do nothing
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// Do nothing
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		
		// Create the ssl socket factory
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new TrustManager[]{ trustManager }, new SecureRandom());
		return context.getSocketFactory();
	}
	
	/**
	 * Not working currently, Jedis throws sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	 * @return
	 */
	private static SSLSocketFactory createRedisSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, IOException {
		// Load redis certificate
		InputStream redisCertInput = ClassLoader.getSystemResourceAsStream("redisCert.crt");
		X509Certificate redisCert = (X509Certificate) CertificateFactory.getInstance("X.509")
		                        .generateCertificate(redisCertInput);
		// Create empty keystore
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		ks.setCertificateEntry("redis", redisCert);
		
		// Create trust store
		TrustManagerFactory trustStore = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustStore.init(ks);
		
		// Create the ssl socket factory
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, trustStore.getTrustManagers(), new SecureRandom());
		return context.getSocketFactory();
	}
	
	private static Integer getNumericEnvVariable(String envVarName) {
		Integer envVarValue = null;
		try {
			envVarValue = Integer.parseInt(System.getenv(envVarName));
		}
		catch (Exception e) {
			System.out.println(String.format("%s environment variable isn't defined", envVarName));
		}
		
		return envVarValue;
	}
}
