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
	private Message message;
	private boolean queuePause;

	public Chat(BotMain botMain, Connection conn) {
		this.botMain = botMain;
		this.conn = conn;
		messageHandler = new MessageHandlerMain(botMain);
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

	/**
	 * Sends a message as fast as possible (pauses the queue in the meantime)
	 * Useful for shutdown command answeres
	 * 	
	 * @param message
	 */
	public void sendNext(String message) {
		try {
			queuePause = true;
			Thread.sleep(350);
			System.out.println("Send:" + botMain.getChannelname() + ": " + message);
			sendRawLine("PRIVMSG " + botMain.getChannelname() + " :" + message + " \r\n");
			if (!botMain.getBotModstate()) {
				Thread.sleep(1200);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		queuePause = false;
		
	}
	
	/**
	 * Messagequeue for sending messages.
	 * Strings that are inserted into the queue are automatically send to their respective channels
	 */
	private void queue() {
		queueMessage = new LinkedList<String>();
		Thread messagequeue = new Thread(new Runnable() {
			public void run() {
				while (botMain.isRunning() || !queueMessage.isEmpty()) {
					try {
						//if queue is not empty and the queue is not paused
						if (!queueMessage.isEmpty() && !queuePause) {
							String message = queueMessage.poll();
							System.out.println("Send:" + botMain.getChannelname() + ": " + message);
							sendRawLine("PRIVMSG " + botMain.getChannelname() + " :" + message + " \r\n");
							int sleeptime = 1550;
							if (botMain.getBotModstate()) {
								sleeptime = 350;
							}
							/*
							if (queueMessage.size() > 20) {
								sleeptime = sleeptime * 2;
							}
							*/
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
						
						//generates messageProperties from in incoming line
						messageProperties = messageHandler.getMessageProperties(line);
						
						if (messageProperties != null) {
							//create a message object which is easier to use than messageProperties.getProperty("XXX");
							message = new Message(messageProperties);
							//Prints out #channel user-name: message
							System.out.println(message.getChannel()+ " " + message.getUserName() + ": " + message.getMessage());
							
							
							//ping command
							if (message.checkProperty("message", new String[] {"!icping","!pingall"}) && message.checkProperty("user-name", botMain.getAdmin())) {
								send(message.getUserName() + ", sure LuL");
							}
							
							//check for shutdown command
							if (message.checkProperty("message", "!icquit") && message.checkProperty("user-name", botMain.getAdmin())) {
								this.sendNext(message.getUserName() + ", " + "Shutting down...");
	
								// So the system has time to send the last message
								try {
									Thread.sleep(300);
								} catch (Exception e) {
									e.printStackTrace();
								}
								System.exit(0);
							}
						}
						 

					}
				}
			} catch (Exception e) {
				System.out.println(e);
				// conn.connect();
			}
		}
	}

}
