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
	 * Receiving of messages
	 */
	public void run() {
		while (true) {
			try {
				while ((line = conn.reader().readLine()) != null) {
					// System.out.println(botMain.getChannelname() + ": " + line);
					if (line.startsWith("PING")) {
						//String extra = line.split(" ", 2)[1];
						//sendRawLine("PONG " + extra);
						sendRawLine("PONG tmi.twitch.tv");
						System.out.println("++++++++++Ping from Twitch answered by " + botMain.getChannelname());
					} else if (line.contains("PONG")) {
						conn.pongReceived();
					}
					System.out.println("From: " + botMain.getChannelname() + ": " + line);
					
					messageProperties = messageHandler.getMessageProperties(line);
					if (messageProperties != null) {
						//Do something with the properties
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

						if (message[1].startsWith("!icquit") && message[0].equals(botMain.getAdmin())) {
							this.send(message[0] + ", " + "Shutting down...");

							// So the system has time to send the last message
							try {
								Thread.sleep(500);
							} catch (Exception e) {
								e.printStackTrace();
							}
							System.exit(0);
						}
					}
					*/
				}
			} catch (Exception e) {
				System.out.println(e);
				// conn.connect();
			}
		}
	}

}
