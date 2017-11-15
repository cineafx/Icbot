package com.cineafx.icbot.main;

import com.cineafx.icbot.bot.BotMain;

/**
 * Builder for creating {@link TwitchBot}s.
 *
 * <p>
 * How to generate:
 * 
 * <pre>
 * TwitchBot bot1 = TwitchBotBuilder.setChannel("#channelname").make();
 * TwitchBot bot2 = TwitchBotBuilder.setNick("CoolBot").setChannel("#channelname")
 * 		.setPassword("oauth:xxxxxxxxxxxxxxxddddddddddddddd").make();
 * </pre>
 *
 * Values which are not defined will be replaced by the default. You can chnage
 * the defaults like this:
 * 
 * <pre>
 * TwitchBotBuilder.setDefaultAdmin("CoolAdmin99");
 * TwitchBotBuilder.setDefaultPort(6667);
 * </pre>
 *
 * </p>
 */
public class TwitchBotBuilder {

	private static String defaultHostname;
	private static Integer defaultPort;
	private static String defaultPassword;
	private static String defaultNick;
	private static String defaultAdmin;
	private static String defaultChannel;

	private String hostname;
	private Integer port;
	private String password;
	private String nick;
	private String admin;
	private String channel;

	/**
	 * private constructor. Use {@link #newBot()} if you want to create a new Bot.
	 */
	private TwitchBotBuilder() {

	}

	/**
	 * start making a new {@link TwitchBot}.
	 * 
	 * @return instance of {@link TwitchBotBuilder}.
	 */
	public static TwitchBotBuilder newBot() {
		return new TwitchBotBuilder();
	}

	/**
	 * set the hostname this bot should use (i. e. irc.twitch.tv)
	 * 
	 * @param hostname
	 * @return this builder
	 */
	public TwitchBotBuilder setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	/**
	 * set the port this bot should use (i. e. 6667)
	 * 
	 * @param port
	 * @return this builder
	 */
	public TwitchBotBuilder setPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * set the oauth this bot should use
	 * 
	 * @param password
	 * @return this builder
	 */
	public TwitchBotBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * set the nickname this bot should use
	 * 
	 * @param nick
	 * @return this builder
	 */
	public TwitchBotBuilder setNick(String nick) {
		this.nick = nick;
		return this;
	}

	/**
	 * set the nick of the admin which will have power over this bot
	 * 
	 * @param admin
	 * @return this builder
	 */
	public TwitchBotBuilder setAdmin(String admin) {
		this.admin = admin;
		return this;
	}

	/**
	 * set the channel this bot should use
	 * 
	 * @see TwitchBot#connect()
	 * @param channel
	 * @return this builder
	 */
	public TwitchBotBuilder setChannel(String channel) {
		this.channel = channel;
		return this;
	}

	/**
	 * Generate and return the TwitchBot
	 * 
	 * @return instance of {@link TwitchBot}.
	 */
	public BotMain make() {
		// use defaults because some values will not differ
		String setThisHostname = defaultHostname;
		int setThisPort = defaultPort;
		String setThisPassword = defaultPassword;
		String setThisNick = defaultNick;
		String setThisAdmin = defaultAdmin;
		String setThisChannel = defaultChannel;

		// override variables defined above in case they are overridden
		if (hostname != null) {
			setThisHostname = hostname;
		}
		if (port != null) {
			setThisPort = port;
		}
		if (password != null) {
			setThisPassword = password;
		}
		if (nick != null) {
			setThisNick = nick;
		}
		if (admin != null) {
			setThisAdmin = admin;
		}
		if (channel != null) {
			setThisChannel = channel;
		}

		// you might want to consider refactoring this constructor
		// i. e. making it package-private and putting it together with the builder in a
		// package
		BotMain returnThis = new BotMain(setThisHostname, setThisPort, setThisPassword, setThisNick, setThisAdmin,
				setThisChannel);
		return returnThis;
	}

	/**
	 * set fallback Hostname. Will be used when {@link #setHostname} is not used
	 * 
	 * @see #setHostname(String)
	 * @param defaultHostname
	 */
	public static void setDefaultHostname(String defaultHostname) {
		TwitchBotBuilder.defaultHostname = defaultHostname;
	}

	/**
	 * set fallback port. Will be used when {@link #setPort} is not used
	 * 
	 * @see #setPort(int)
	 * @param defaultPort
	 */
	public static void setDefaultPort(int defaultPort) {
		TwitchBotBuilder.defaultPort = defaultPort;
	}

	/**
	 * set fallback password. Will be used when {@link #setPassword} is not used
	 * 
	 * @see #setPassword(String)
	 * @param defaultPassword
	 */
	public static void setDefaultPassword(String defaultPassword) {
		TwitchBotBuilder.defaultPassword = defaultPassword;
	}

	/**
	 * set fallback nick. Will be used when {@link #setNick} is not used
	 * 
	 * @see #setNick(String)
	 * @param defaultNick
	 */
	public static void setDefaultNick(String defaultNick) {
		TwitchBotBuilder.defaultNick = defaultNick;
	}

	/**
	 * set fallback admin. Will be used when {@link #setAdmin} is not used.
	 * 
	 * @see #setAdmin(String)
	 * @param defaultAdmin
	 */
	public static void setDefaultAdmin(String defaultAdmin) {
		TwitchBotBuilder.defaultAdmin = defaultAdmin;
	}

	/**
	 * set fallback channel. Will be used when {@link #setChannel} is not used.
	 * 
	 * @see #setChannel(String)
	 * @param defaultChannel
	 */
	public static void setDefaultChannel(String defaultChannel) {
		TwitchBotBuilder.defaultChannel = defaultChannel;
	}
}