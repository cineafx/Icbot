package com.cineafx.icbot.bot.messageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.cineafx.icbot.bot.BotMain;
import com.cineafx.icbot.sql.SqlCommands;

public class CommandHandler {
	
	private BotMain botMain;
	private SqlCommands sqlcommands;
	private Properties timeoutProperties;
	
	private List<String> commandCache = new ArrayList<String>();

	public CommandHandler(BotMain botMain) {
		this.botMain = botMain;
		sqlcommands = new SqlCommands(botMain.getSqlServername(), botMain.getSqlUsername(), botMain.getSqlPassword(), botMain.getSqlDbname());
		timeoutProperties = new Properties();
	}

	/**
	 * Check whether a command is present inside the messageProperties
	 * @param messageProperties
	 * @return response or null
	 */
	public String checkForCommand(Properties messageProperties) {
		String firstWord = messageProperties.getProperty("message").split(" ",2)[0];
		String returnArray[] = new String[4];
		if (!firstWord.isEmpty()) {
			
			//If 30 seconds have passed since the latest command update refresh the local command cache list
			if (botMain.getlastCommandCacheUpdate() + 60000 < System.currentTimeMillis()) {
				commandCache = sqlcommands.updateCommandCache();
				botMain.updateLastCommandCacheUpdate();
			}
			
			if (commandCache.contains(firstWord)) {
				returnArray = sqlcommands.getCommand(firstWord, botMain.getChannelname());

				//no command from input string
				if (returnArray != null) {
					//is userlevel even permitted
					if (Integer.parseInt(returnArray[2]) <= this.checkUserLevel(messageProperties)) {
						//check timeout
						if (this.handleCommandTimeout(returnArray)) {
							sqlcommands.updateTimesUsed(returnArray[0]);
							return checkForInserts(returnArray, messageProperties);
						}
					}
				}
			}
			
				
		}
		return null;
	}
	
	/**
	 * Checks if the timeout of the command is already over
	 * 
	 * @param returnArray
	 * @return is allowed 
	 */
	private boolean handleCommandTimeout(String[] returnArray) {
		//get last time command was used (never defaults to 0)
		Long lastTimeUsed = Long.parseLong(timeoutProperties.getProperty(returnArray[0], "0"));
		//check the property
		if ((lastTimeUsed + Long.parseLong(returnArray[3]) * 1000) < System.currentTimeMillis()) {
			//update timeout
			timeoutProperties.setProperty(returnArray[0], "" + System.currentTimeMillis());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the userlevel as integer
	 * 
	 * @param messageProperties
	 * @return userLevel
	 * <ol start = "0">
	 * 	<li>pleb</li>
	 * 	<li>sub</li>
	 * 	<li>mod</li>
	 * 	<li>broadcaster</li>
	 *  <li>admin (of the bot)</li>
	 * </ol>
	 */
	private int checkUserLevel(Properties messageProperties) {
		//default: pleb
		int userlevel = 0;
		if (messageProperties.getProperty("user-name", "NULL").equals(botMain.getAdmin())) {
			//admin
			userlevel = 4;
		} else if(messageProperties.getProperty("@badges","NULL").contains("broadcaster")) {
			//broadcaster
			userlevel = 3;
		} else if(messageProperties.getProperty("mod","NULL").equals("1")) {
			//moderator
			userlevel = 2;
		} else if(messageProperties.getProperty("subscriber", "NULL").equals("1")) {
			//subscriber
			userlevel = 1;
		} 
		return userlevel;
	}

	/**
	 * This function check for custom parameter in commands still WIP / TODO
	 * @param returnArray
	 * @param messageProperties
	 * @return returnString
	 */
	private String checkForInserts(String[] returnArray, Properties messageProperties) {
		
		return returnArray[1];
	}
}
