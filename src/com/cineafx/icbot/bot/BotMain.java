package com.cineafx.icbot.bot;

public class BotMain {
	private String hostname;
	private int port;
	private String password;
	private String nick;
	private String admin = "";
	private String channel = "";
	private boolean running;
	private boolean isMod;

	// public Connection conn;
	// public Chat chat;

	public BotMain(String hostname, int port, String password, String nick, String admin, String channel) {
		this.hostname = hostname;
		this.port = port;
		this.password = password;
		this.nick = nick;
		this.admin = admin;
		this.channel = channel;
		// conn = new Connection(this);
	}
	
	/**
	 * First time setup (connects to it's corresponding channel)
	 */
	public void init() {
		//conn.connect();
	}

	/**
	 * Returns the channelname of the bot
	 *
	 * @return Channelname of the bot
	 */
	public String getChannelname() {
		return this.channel;
	}

	/**
	 * Set the running boolean
	 *
	 * @param running
	 *            boolean
	 */
	public void setRunning(boolean run) {
		running = run;
	}

	/**
	 * Returns the running boolean
	 *
	 * @return running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Sees if the bot / channel should handle the ping
	 *
	 * @return if return true this bot / channel is set to main
	 */
	public boolean isMainChannel() {
		if (nick.equals(channel.substring(1))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the hostname to connect to the twitch irc-server
	 *
	 * @return String hostname
	 */
	public String getHostname() {
		return this.hostname;
	}

	/**
	 * Returns the port to connect to the twitch irc-server
	 *
	 * @return int port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns the pasword / oauth to connect to the twitch irc-server
	 *
	 * @return String password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Returns the username of the admin
	 *
	 * @return String admin username
	 */
	public String getAdmin() {
		return this.admin;
	}
	
	/**
	 * Returns the username of the bot
	 *
	 * @return String nick
	 */
	public String getNick() {
		return this.nick;
	}
	
	/**
	 * Returns whether the bot is a moderator in the channel
	 *
	 * @return isMod
	 */	
	public boolean getBotModstate() {
		return isMod;
	}
	
	/**
	 * Set the state of whether the bot is a mod
	 * 
	 * @param modstate
	 */
	public void setBotModstate(boolean modstate) {
		isMod = modstate;
	}
}
