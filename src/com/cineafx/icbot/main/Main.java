package com.cineafx.icbot.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import com.cineafx.icbot.bot.*;

public class Main {

	public Main() throws Exception {
		// read properties from file
		Properties properties = getProperties();
		//apply default settings for the twitchbot builder
		defineBuilderDefaults(properties);
		System.out.println(properties);
		
		//create a new list for all bots
		List<BotMain> bots = new ArrayList<>();
		
		while (true) {
			//temporary way of adding channels (until sql is done)
			List<String> channels = new ArrayList<>();
			channels.add(0,"#icbot47");					//adds mainChannel to the first position of the arraylist
			channels.add("#cineafx");					//temporary adding of second channel
			channels.add("#pajlada");					//temporary adding of second channel
			
			// remove bots that are not present in the database
			for (int i = 0; i < bots.size(); i++) {
				boolean exists = false;
				//checks if channel is supposed to exist
				if (channels.contains(bots.get(i).getChannelname())) {
					exists = true;
				}
				if (!exists) {
					//removes bot
					System.out.println("Removed: " + bots.get(i).getChannelname());
					bots.remove(i);
				}
			}

			// add bots that are present in the database
			for (String channel : channels) {
				boolean exists = false;
				//checks if channel exist
				for (int i = 0; i < bots.size(); i++) {
					if (channel.equals(bots.get(i).getChannelname())) {
						exists = true;
					}
				}
				if (!exists) {
					//creates new bot
					BotMain newBot = TwitchBotBuilder.newBot().setChannel(channel).make();
					//first time setup of the bot
					newBot.init();
					//adds bot to bot array
					bots.add(newBot);
					System.out.println("Added: " + channel);
				}
			}
			//waits 10 seconds
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/**
	 * Defines the default settings for the twitchbots
	 */
	private static void defineBuilderDefaults(Properties properties) {
		TwitchBotBuilder.setDefaultHostname("irc.twitch.tv");
		TwitchBotBuilder.setDefaultPort(6667);

		String oauthString = properties.getProperty("oauth");
		TwitchBotBuilder.setDefaultPassword(oauthString);

		String nickString = properties.getProperty("nick");
		TwitchBotBuilder.setDefaultNick(nickString);

		String adminString = properties.getProperty("admin");
		TwitchBotBuilder.setDefaultAdmin(adminString);
	}

	/**
	 * read the connection settings from a config.properties file
	 */
	private static Properties getProperties() throws IOException {
		String propertiesName = "config.properties";
		Properties properties = new Properties();

		try (FileInputStream fileInputStream = new FileInputStream(propertiesName);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
			properties.load(bufferedInputStream);
		}
		return properties;
	}

	public static void main(String[] args) throws Exception {
		new Main();
	}

}
