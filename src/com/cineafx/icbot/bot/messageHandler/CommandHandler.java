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

	public String checkForCommand(Properties messageProperties) {
		String firstWord = messageProperties.getProperty("message").split(" ",2)[0];
		String returnArray[] = new String[4];
		if (!firstWord.isEmpty()) {
			//gets an array of responses for the 
			returnArray = sqlcommands.getCommand(firstWord, botMain.getChannelname());
			//no command from input string
			if (returnArray != null) {
				//is userlevel even permitted
				if (Integer.parseInt(returnArray[1]) <= this.checkUserLevel(messageProperties)) {
					
					//just returns atm .... doesn't check timeout TODO: continue here
					return returnArray[0];
				}
			}
		}
		return null;
	}

	private int checkUserLevel(Properties messageProperties) {
		
		return 0;
	}
}
