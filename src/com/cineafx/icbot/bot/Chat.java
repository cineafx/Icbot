package com.cineafx.icbot.bot;

import java.util.LinkedList;
import java.util.Queue;

import com.cineafx.icbot.bot.messageHandler.MessageHandlerMain;

public class Chat implements Runnable {

	private BotMain botMain;
	private Connection conn;
	private MessageHandlerMain messageHandler;


	private Queue<String> queueMessage;
	private String line = "";
	private boolean queuePause;
	private boolean addSpecialChar = false;

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
							if (addSpecialChar) {
								//This will make sure the "Message send in the last 30 seconds" stuff doesn't prevent from posting
								message = message + " \u206D";
							}
							addSpecialChar = !addSpecialChar;
							System.out.println("Send:" + botMain.getChannelname() + ": " + message);
							sendRawLine("PRIVMSG " + botMain.getChannelname() + " :" + message + " \r\n");
							int sleeptime = 1550;
							if (botMain.getBotModstate()) {
								sleeptime = 350;
							}
							Thread.sleep(sleeptime);
						} else {
							Thread.sleep(10);
						}
					} catch (Exception e) {
						System.out.println(e);
					}
				}
				// So the system has time to send any last messages send by sendNext()
				try {
					Thread.sleep(300);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.exit(0);
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
			String returnMessage;
			try {
				//repeat while a new line can be read
			while ((line = conn.reader().readLine()) != null) {
					//System.out.println("From: " + botMain.getChannelname() + ": " + line);
					if (line.startsWith("PING")) {
						sendRawLine("PONG tmi.twitch.tv");
						System.out.println("++++++++++Ping from Twitch answered by " + botMain.getChannelname());
					} else if (line.contains("PONG")) {
						conn.pongReceived();
					} else {
						
						//long endtime = 0;
						//long starttime = System.nanoTime();
						
						//send the input line to the messagehandler which will return the string to send out to the irc server
						returnMessage = messageHandler.handleMessage(line);
						
						//endtime = System.nanoTime();
						//System.out.println("____________Message processing time: " + (((double)endtime - (double)starttime)/1000000) + " ms\n");
						
						//only send the returnMessage isn't null and isn't an empty string (after trimming)
						if (returnMessage != null && !returnMessage.trim().isEmpty()) {
							send(returnMessage);
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
