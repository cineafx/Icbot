package com.cineafx.icbot.bot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Connection implements Runnable {

	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;

	private BotMain botMain;
	public Chat chat;

	private long lastPing = -1;

	public Connection(BotMain botMain) {
		this.botMain = botMain;
	}

	/**
	 * Connects to the twitch irc server and then joins the channel this object
	 * is assigned to
	 */
	public void connect() {
		try {
			socket = new Socket(botMain.getHostname(), botMain.getPort());
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			botMain.setRunning(true);
			chat = new Chat(botMain, this);
			chat.sendRawLine("PASS " + botMain.getPassword() + "\r\n");
			chat.sendRawLine("NICK " + botMain.getNick() + "\r\n");
			chat.sendRawLine("CAP REQ :twitch.tv/commands\r\n");
			chat.sendRawLine("CAP REQ :twitch.tv/tags\r\n");

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("004") >= 0) {
					// We are now logged in.
					break;
				} else if (line.indexOf("433") >= 0) {
					System.out.println("Nickname is already in use.");
					System.exit(1);
					return;
				}
			}

			join();
			lastPing = System.currentTimeMillis();
			new Thread(this).start();
		} catch (Exception e) {
			System.out.println(e);
			botMain.setRunning(false);
		}
	}

	/**
	 * (Re)joines a chat
	 *
	 * @param channel
	 *            The channel to (re)join
	 */
	public void join() {
		String channel = botMain.getChannelname();
		chat.sendRawLine("PART " + channel);
		chat.sendRawLine("JOIN " + channel);
		botMain.setRunning(true);
		System.out.println("Suggessfully joined: " + channel);
	}

	/*
	 * Used in com.cineafx.icbot.bot Chat.java
	 */
	public BufferedWriter writer() {
		return writer;
	}

	public BufferedReader reader() {
		return reader;
	}

	/**
	 * Sets the lastping to the current time in Milliseconds
	 */
	public void pongReceived() {
		this.lastPing = System.currentTimeMillis();
	}


	public void run() {
		int timeout = 0;
		while (botMain.isRunning()) {
			try {
				long timePing = System.currentTimeMillis();
				if (botMain.isMainChannel()) {
					chat.sendRawLine("PING twitch");
				}
				Thread.sleep(1000);
				//System.out.println("Ping time: " + (lastPing - timePing));
				if ((lastPing- timePing) > 1500) {
					timeout++;
					if (timeout > 10) {
						botMain.setRunning(false);
						join();
					}
					System.out.println("----------No Ping recieved at: " + botMain.getChannelname() + " for "
							+ timeout * 2 + " seconds. (Not correct value)");
					Thread.sleep(1000);
				} else {
					Thread.sleep(58000);
					timeout = 0;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

}