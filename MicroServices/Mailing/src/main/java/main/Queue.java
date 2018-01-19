package main;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

public class Queue {
	private final String QUEUE_NAME = "checkout";
	private final String HOST_NAME = "portal-ssl292-6.bmix-lon-yp-0f59cdb0-ff8b-4718-9a7c-b0f8bb557253.benamiamit0-gmail-com.composedb.com";
	private final String VIRTUAL_HOST = "bmix-lon-yp-0f59cdb0-ff8b-4718-9a7c-b0f8bb557253";
	private final int PORT_NUMBER = 22817;
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private QueueSubscriber subscriber;

	public Queue(QueueSubscriber subscriber) {
		this.subscriber = subscriber;
		
		try {
			this.factory = new ConnectionFactory();
			// this.factory.setUsername("broker");
			// this.factory.setPassword("3debc146994381c37f8da8e538fd39548f0c1158316fd11f3f6");
			// this.factory.setPassword("A9Knr2TS`/Nc~mTD@z`/Hk<5[bS");
			// this.factory.setVirtualHost(VIRTUAL_HOST);
			// this.factory.setHost(HOST_NAME);
			// this.factory.setPort(PORT_NUMBER);
			this.factory.setUri(
					"amqps://admin:3debc146994381c37f8da8e538fd39548f0c1158316fd11f3f6@portal-ssl292-6.bmix-lon-yp-0f59cdb0-ff8b-4718-9a7c-b0f8bb557253.benamiamit0-gmail-com.composedb.com:22817/bmix-lon-yp-0f59cdb0-ff8b-4718-9a7c-b0f8bb557253");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		this.initQueue();
	}

	private void initQueue() {
		try {
			this.connection = this.factory.newConnection();
			this.channel = this.connection.createChannel();
			this.channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		} catch (TimeoutException e) {
			System.out.println("Timeout exception: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getMessage());
		}
	}

	public void sendMessage(String message) {
		try {
			this.channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			System.out.println("[x] sent '" + message + "'");
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getMessage());
		}
	}

	public void listen() {
		final Queue _this = this;
		try {
			Consumer consumer = new DefaultConsumer(this.channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println("[x] received '" + message + "'");
					_this.subscriber.getMessage(message);
				}
			};

			this.channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
