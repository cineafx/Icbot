package com.cineafx.icbot.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.cineafx.icbot.bot.*;
import com.cineafx.icbot.sql.*;

public class Main implements Runnable {

	private SqlChannels sqlChannels;
	private boolean startupComplete = false;

	public Main() throws Exception {
		new Thread(this).start();
		
		// read properties from file
		Properties properties = getProperties();
		//apply default settings for the twitchbot builder
		defineBuilderDefaults(properties);
		System.out.println(properties);

		//create a new list for all bots
		List<BotMain> bots = new ArrayList<>();

		String sqlserver = properties.getProperty("sqlserver");
		String sqluser = properties.getProperty("sqluser");
		String sqlpass = properties.getProperty("sqlpass");
		String sqldbname = properties.getProperty("sqldbname");
		
		//This is what decides whether the entire bot shuts down
		boolean globalRunning = true;

		//if not all required infos are there exit the program
		if (sqlserver.isEmpty() || sqluser.isEmpty() || sqlpass.isEmpty() || sqldbname.isEmpty()) {
			System.exit(78);
		}

		sqlChannels = new SqlChannels(sqlserver, sqluser, sqlpass, sqldbname);
		
		int loopcounter = 0;
		while (globalRunning) {
			//temporary way of adding channels (until sql is done)
			List<String> channels = new ArrayList<>();

			//adds it's own channel to the first position of the arraylist
			channels.add(0,"#" + properties.getProperty("nick"));		

			//add the channels received by an sql query
			channels.addAll(Arrays.asList(sqlChannels.getChannels()));
			
			//Every tenth loop
			if (loopcounter % 10 == 0) {
				// remove bots that are not present in the database
				for (int i = 0; i < bots.size(); i++) {
					//checks if channel isn't supposed to exist
					if (!channels.contains(bots.get(i).getChannelname())) {
						//removes bot
						System.out.println("Removed: " + bots.get(i).getChannelname());
						bots.get(i).setRunning(false);
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

				loopcounter = 0;
			}
			
			for (int i = 0; i < bots.size(); i++) {
				//checks if channel received a global shutdown command
				if (bots.get(i).getGlobalShutdown()) {
					System.out.println("###################---SHUTDOWN---###################");
					System.out.println("Global shutdown from: " + bots.get(i).getChannelname());
					//stops all bots after their message queue is empty
					for (BotMain bot : bots) {
						bot.setRunning(false);
					}
					globalRunning = false;
				}
			}
			
			
			startupComplete = true;
			//if while loop is going to stop anyway, there is no need to wait 
			//Will still wait 2 seconds to let all other bots do their "shutdown procedure" (Send last message)
			if (globalRunning) {
				//waits 3 seconds
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
			loopcounter++;
		}
		System.out.println("Shutdown...");
		System.exit(0);
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

		String sqlserverString = properties.getProperty("sqlserver");
		TwitchBotBuilder.setDefaultSqlServername(sqlserverString);

		String sqluserString = properties.getProperty("sqluser");
		TwitchBotBuilder.setDefaultSqlUsername(sqluserString);

		String sqlpassString = properties.getProperty("sqlpass");
		TwitchBotBuilder.setDefaultSqlPassword(sqlpassString);

		String sqldbnameString = properties.getProperty("sqldbname");
		TwitchBotBuilder.setDefaultSqlDbname(sqldbnameString);
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

	//Will check if bot started successfully and if not stop the bot
	public void run() {
		try {
			//Wait 15 seconds
			Thread.sleep(15000);
			if (!startupComplete) {
				System.out.println("ERROR during startup\nShutting down...");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
