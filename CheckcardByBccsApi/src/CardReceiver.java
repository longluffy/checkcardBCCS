
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.SerializationUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import cardprocess.hibernate.CardProcess;

public class CardReceiver {

	private final static String RECEIVE_QUEUE_NAME = "CheckcardService_request";
	private final static String RESPONSE_QUEUE_NAME = "CheckcardService_response";
	static CardProcess cardInputstatic = null;
	static String jseesionid1;
	static String jseesionid2;

	public static void main(String[] argv)
			throws java.io.IOException, java.lang.InterruptedException, TimeoutException {

		ConnectionFactory factory = new ConnectionFactory();
		String userName = "longluffy";
		String password = "12345678";
		String virtualHost = "/";
		int portNumber = 5672;
		String hostName = "27.72.30.109";
		factory.setUsername(userName);
		factory.setPassword(password);
		factory.setVirtualHost(virtualHost);
		factory.setHost(hostName);
		factory.setPort(portNumber);

		jseesionid1 = argv[0];
		jseesionid2 = argv[1];

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(RECEIVE_QUEUE_NAME, false, false, false, null);
		System.out.println(
				" [*] CheckCardReceiver Waiting for messages on " + RECEIVE_QUEUE_NAME + "  . To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				System.out.println(" [x] CardReceiver on " + LocalDateTime.now() + " Received ");

				Connection connection_result = null;
				CardProcess cardInput = SerializationUtils.deserialize(body);
				System.out.println(" serial =" + cardInput.getSerial() + " pin =" + cardInput.getPin());
				try {
					// TODO check card message
					// jseesionid1 = "60E75FC3D5D30F130CF011F0D8B08E90.BCCS_CC2_51_8686";
					// jseesionid2 = "2B87D961F00F974EFFF2C057DDCF9F7E.BCCS_CC2_51_8686";

					sendPostCheckcard cardservice = new sendPostCheckcard();
					cardservice.sendPost(jseesionid1, jseesionid2, cardInput);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					cardInput.setCardavailable(0);
					cardInput.setCardcheckresult(e.toString());
				} finally {
					try {
						connection_result = factory.newConnection();
						Channel channel_res = connection_result.createChannel();
						channel_res.queueDeclare(RESPONSE_QUEUE_NAME, false, false, false, null);
						byte[] data = SerializationUtils.serialize(cardInput);
						channel_res.basicPublish("", RESPONSE_QUEUE_NAME, null, data);

						System.out.println("result sent to " + RESPONSE_QUEUE_NAME + ": " + cardInput.getSerial()
								+ "  State : " + cardInput.getCardavailable() + " " + cardInput.getCardcheckresult());
						channel_res.close();
						connection_result.close();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Failed to response to server");
					}
					System.out.println("");
				}

			}

		};
		channel.basicConsume(RECEIVE_QUEUE_NAME, true, consumer);

		// Keep session open
		Thread threadKeepSession = new Thread() {
			public void run() {
				while (true) {
					try {

//						CookieManager cookieManager = new CookieManager();
//						CookieHandler.setDefault(cookieManager);

						System.out.println("request home page to Keep session open at " + LocalDateTime.now());
						URL url = new URL("http://10.240.147.246/BCCS_CC/home");
						URLConnection con = url.openConnection();

						InputStream is = con.getInputStream();
						BufferedReader in = new BufferedReader(new InputStreamReader(is));
						String inputLine;
						while ((inputLine = in.readLine()) != null)
							inputLine += "";
						in.close();
						is.close();

						con.getContent();

//						http://10.240.147.246/BCCS_CC/stockVtracking.jsf
						URL url1 = new URL("http://10.240.147.246/BCCS_CC/stockVtracking.jsf");
						URLConnection con1 = url1.openConnection();

						InputStream is1 = con1.getInputStream();
						BufferedReader in1 = new BufferedReader(new InputStreamReader(is1));
						while ((inputLine = in1.readLine()) != null)
							inputLine += "";
						in1.close();
						is1.close();

						Thread.sleep(60000l);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		};

		threadKeepSession.start();

	}

}
