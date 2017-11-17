package com.cineafx.icbot.bot;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import com.cineafx.icbot.bot.messageHandler.MessageHandlerMain;

public class Chat implements Runnable {

	private BotMain botMain;
	private Connection conn;
	private MessageHandlerMain messageHandler;
	
	//public Clean cleanMessage;
	//public Commands com;
	//public Moderate mod;

	private Queue<String> queueMessage;
	private String line = "";
	private Properties messageProperties = new Properties();

	public Chat(BotMain botMain, Connection conn) {
		this.botMain = botMain;
		this.conn = conn;
		messageHandler = new MessageHandlerMain();
		//cleanMessage = new Clean(botMain);
		//com = new Commands(botMain);
		//mod = new Moderate(botMain);
		this.queue();
		new Thread(this).start();
	}

	/**
	 * Adds a new message to the queue
	 *
	 * @param message
	 */
	public void send(String message) {
		this.queueMessage.add(message);
	}

	private void queue() {
		queueMessage = new LinkedList<String>();
		Thread messagequeue = new Thread(new Runnable() {
			public void run() {
				while (botMain.isRunning() || !queueMessage.isEmpty()) {
					try {
						if (!queueMessage.isEmpty()) {
							String message = queueMessage.poll();
							System.out.println("Send:" + botMain.getChannelname() + ": " + message);
							sendRawLine("PRIVMSG " + botMain.getChannelname() + " :" + message + " \r\n");
							//conn.writer().write("PRIVMSG " + botMain.getChannelname() + " :" + message + " \r\n");
							//conn.writer().flush();
							int sleeptime = 2000;
							if (botMain.getBotModstate()) {
								sleeptime = 500;
							}
							if (queueMessage.size() > 20) {
								sleeptime = sleeptime * 2;
							}
							Thread.sleep(sleeptime);
						} else {
							Thread.sleep(10);
						}
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
		});
		messagequeue.start();
	}

	/**
	 * Sends a raw line to the twitch irc
	 * Adds "\r\n" if it doesn't contain it
	 *
	 * @param message
	 */
	public void sendRawLine(String message) {
		try {
			System.out.println("To: " + botMain.getChannelname() + ": " + message);
			if (message.endsWith("\r\n")) {
				conn.writer().write(message);
			} else {
				conn.writer().write(message + "\r\n");
			}
			conn.writer().flush();
		} catch (Exception e) {
			System.out.println(e);
			conn.connect();
		}

	}
	
	/**
	 * Returns whether a value of the current property name is equals to the value to check for<br>
	 * property names can be found here: {@link com.cineafx.icbot.bot.messageHandler.MessageHandlerMain#getMessageProperties(String)}
	 * 
	 * @param propertyName
	 * @param valueToCheckFor
	 * @return boolean
	 */
	private boolean checkProperty(String propertyName, String valueToCheckFor) {
		return messageProperties.getProperty(propertyName).equals(valueToCheckFor);
	}
	
	/**
	 * Returns whether a value of the current property name is equals to the value to check for<br>
	 * property names can be found here: {@link com.cineafx.icbot.bot.messageHandler.MessageHandlerMain#getMessageProperties(String)}<br>
	 * Use the following code example to create nameless String arrays<pre>
	 * <code> checkProperty("propertyName", new String[] {"value1","value2"});</code></pre>
	 * 
	 * @param propertyName
	 * @param valuesToCheckFor
	 * @return boolean
	 */
	private boolean checkProperty(String propertyName, String[] valuesToCheckFor) {
		for (String string : valuesToCheckFor) {
			if (messageProperties.getProperty(propertyName).equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Receiving of messages
	 */
	public void run() {
		while (true) {
			try {
				//repeat while a new line can be read
				while ((line = conn.reader().readLine()) != null) {
					//System.out.println("From: " + botMain.getChannelname() + ": " + line);
					if (line.startsWith("PING")) {
						//String extra = line.split("[ ;]", 2)[1];
						//sendRawLine("PONG " + extra);
						sendRawLine("PONG tmi.twitch.tv");
						System.out.println("++++++++++Ping from Twitch answered by " + botMain.getChannelname());
					} else if (line.contains("PONG")) {
						conn.pongReceived();
					} else {
						
						messageProperties = messageHandler.getMessageProperties(line);
						if (messageProperties != null) {
							//Do something with the properties
							
							if (checkProperty("message", new String[] {"!icping","!pingall"})) {
								send(messageProperties.getProperty("user-name") + ", sure LuL");
							}
							System.out.println(messageProperties.getProperty("channel") + " " + messageProperties.getProperty("user-name") + ": " + messageProperties.getProperty("message"));
							
							//check for shutdown command
							if (checkProperty("message", "!icquit") && checkProperty("user-name", botMain.getAdmin())) {
								this.send(messageProperties.getProperty("user-name") + ", " + "Shutting down...");
	
								// So the system has time to send the last message
								try {
									Thread.sleep(500);
								} catch (Exception e) {
									e.printStackTrace();
								}
								System.exit(0);
							}
						}
					
					
					
						/*
						String[] message = cleanMessage.clean(line);
						if (message != null) {
							System.out.println(botMain.getChannelname() + ": " + message[0] + "(" + message[2] + "|"
									+ message[3] + "): " + message[1]);
	
							String answer = mod.check(message);
							if (answer == null) {
								answer = com.check(message);
							}
							if (answer != null) {
								this.send(answer);
							}
	
							
						}
						 */
					}
				}
			} catch (Exception e) {
				System.out.println(e);
				// conn.connect();
			}
		}
	}

}
