package com.cineafx.icbot.bot.messageHandler;

import java.util.Properties;

import com.cineafx.icbot.bot.BotMain;
import com.cineafx.icbot.sql.SqlCommands;

public class CommandHandler {
	
	private BotMain botMain;
	private SqlCommands sqlcommands;

	public CommandHandler(BotMain botMain) {
		this.botMain = botMain;
		sqlcommands = new SqlCommands(botMain.getSqlServername(), botMain.getSqlUsername(), botMain.getSqlPassword(), botMain.getSqlDbname());
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
			//gets an array of responses for the 
			returnArray = sqlcommands.getCommand(firstWord, botMain.getChannelname());
			//no command from input string
			if (returnArray != null) {
				//is userlevel even permitted
				if (Integer.parseInt(returnArray[2]) <= this.checkUserLevel(messageProperties)) {
					//check timeout
					//TODO: implement it properly
					if (true) {
						
						sqlcommands.updateTimesUsed(returnArray[0]);
						return returnArray[1];
					}
					
				}
			}
		}
		return null;
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
}
