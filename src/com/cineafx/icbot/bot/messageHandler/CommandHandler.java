package com.cineafx.icbot.bot.messageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cineafx.icbot.bot.BotMain;
import com.cineafx.icbot.sql.SqlCommands;

public class CommandHandler {
	
	private BotMain botMain;
	SpecialCommandHandler specialCommandHandler;
	private SqlCommands sqlcommands;
	private Properties timeoutProperties;
	
	private List<String> commandCache = new ArrayList<String>();
	
	private int maxAllowedInserts = 20;

	public CommandHandler(BotMain botMain) {
		this.botMain = botMain;
		specialCommandHandler = new SpecialCommandHandler();
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
		String returnString = returnArray[1];
		
		int i = 0;
		while (returnString.contains("${") && i < maxAllowedInserts) {
			returnString = returnArray[1];

			//${user}
			if (returnString.toLowerCase().contains("${user}")) {
				returnString = returnString.replaceAll("(?i)\\$\\{user\\}", 
						messageProperties.getProperty("user-name", "ERROR"));
			}
			
			//${channel}
			if (returnString.toLowerCase().contains("${channel}")) {
				returnString = returnString.replaceAll("(?i)\\$\\{channel\\}", 
						messageProperties.getProperty("channel", "ERROR").substring(1));
			}
			
			//${botuptime}
			if (returnString.toLowerCase().contains("${botuptime}")) {
				returnString = returnString.replaceAll("(?i)\\$\\{botuptime\\}", 
						specialCommandHandler.botUpTime());
			}
			
			//${url=.*}
			if (returnString.toLowerCase().contains("${url=")) {
				//will return the url from the ${url= XXX } command
				Pattern pattern = Pattern.compile("(?i)\\$\\{url=(.*?)\\}");
				Matcher matcher = pattern.matcher(returnString);
				matcher.find();
				
				returnString = matcher.replaceFirst(specialCommandHandler.getFromURL(matcher.group(1)));
			}
			
			//${randomint(min,max)}
			if (returnString.toLowerCase().contains("${randomint(")) {
				Pattern pattern = Pattern.compile("(?i)\\$\\{randomint\\((\\d+?),(\\d+?)\\)\\}");
				Matcher matcher = pattern.matcher(returnString);
				matcher.find();
				
				returnString = matcher.replaceFirst(specialCommandHandler.randomInt(matcher.group(1), matcher.group(2)));
			}
			
			//${randomdouble(min,max,digits)}
			if (returnString.toLowerCase().contains("${randomdouble(")) {
				Pattern pattern = Pattern.compile("(?i)\\$\\{randomdouble\\((\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?),(\\d+?)\\)\\}");
				Matcher matcher = pattern.matcher(returnString);
				matcher.find();
				
				returnString = matcher.replaceFirst(specialCommandHandler.randomDouble(matcher.group(1), matcher.group(3), matcher.group(5)));
			}

			i++;
		}
		return returnString;
	}
}
