package com.cineafx.icbot.bot.messageHandler;

import java.util.Arrays;
import java.util.Properties;

import com.cineafx.icbot.bot.BotMain;

public class MessageHandlerMain {
	
	private BotMain botMain;
	
	private static String delimSpace = "[ ]";
	private static String delimSemicolon = "[;]";
	private static String[] propertiesNamesPRIVMSG = {"user-name","messagetype", "channel","message"};
	private static String[] propertiesNamesUSERSTATE = {"", "messagetype", "channel"};

	public MessageHandlerMain(BotMain botMain) {
		this.botMain = botMain;
	}
	
	/**
	 * Gives back the input stream (raw message from twitch irc server) split into the single properties:<br>
	 * <br>
	 * <ul>
	 * <li>user-name</li>
	 * <li>messagetype</li>
	 * <li>channel</li>
	 * <li>message</li>
	 * <br>
	 * <li>@badges=</li>
	 * <li>color=</li>
	 * <li>display-name=</li>
	 * <li>emotes=</li>
	 * <li>id=</li>
	 * <li>mod=</li>
	 * <li>room-id=</li>
	 * <li>subscriber=</li>
	 * <li>tmi-sent-ts=</li>
	 * <li>turbo=</li>
	 * <li>user-id=</li>
	 * <li>user-type=</li>
	 * </ul>
	 * 
	 * @param input
	 * @return properties
	 */
	public Properties getMessageProperties(String input) {
		Properties messageProperties = new Properties();
		
		//get all but the first token
		String[] tokenSpace = this.splitSpace(input);
		

		//if there is no relevant token return null
		if (tokenSpace == null) {
			return null;
		}	
		
		//gets the content of the first tokenSpace as an array split at ";"
		String[] tokenSemicolon = splitSemicolon(tokenSpace[0]);
		
		
		//removes the first element of the tokenSpace array
		tokenSpace = Arrays.copyOfRange(tokenSpace, 1, tokenSpace.length);
	
		
		//set the properties for all other available properties
		for (int i = 0; i < tokenSemicolon.length; i++) {
			//split the parameter into name and value
			String[] propertieToken = tokenSemicolon[i].split("[=]", 2);
			//add the properties 
			messageProperties.setProperty(propertieToken[0], propertieToken[1]);
		}
		
		if (tokenSpace.length == 4) {
			//PRIVMSG
			
			//set the properties for username, messagetype, channelname, message
			for (int i = 0; i < tokenSpace.length; i++) {
				messageProperties.setProperty(propertiesNamesPRIVMSG[i], tokenSpace[i]);
			}	
			
		} else if(tokenSpace.length == 3) {
			//USERSTATE 
			
			//set the properties for messagetype, channelname 
			for (int i = 1; i < tokenSpace.length; i++) {
				messageProperties.setProperty(propertiesNamesUSERSTATE[i], tokenSpace[i]);
			}	
			
		}
	
			
		System.out.println(messageProperties);
		
		//check how to handle the parameter depending on the messagetpye
		if (messageProperties.getProperty("messagetype","NULL").equals("PRIVMSG")) {

			//return all the properties
			return messageProperties;
			
			
		} else if (messageProperties.getProperty("messagetype","NULL").equals("USERSTATE")) {
			//update things like the mod status of the bot			
			usercheck(messageProperties);
		} 
		//return null in case it isn't "PRIVMSG"
		return null;
				
	}
	
	/**
	 * splits the input string with the delimSpace regex 
	 * @param input
	 * @return String[]
	 */
	private String[] splitSpace(String input) {
		//Delimit the input array by spaces
		String[] tokenSpace = input.split(delimSpace, 5);
		
		//checks whether the incoming message is a PRIVMSG (normal chat message) 
		//or a USERSTATE message (used to check for mod / broadcaster status
		if (tokenSpace[2].equals("PRIVMSG")) {
			//only keep everything between the ":" and the "!" to get the clean username
			tokenSpace[1] = tokenSpace[1].substring(1, tokenSpace[1].indexOf('!'));
			//remove the leading ":" from the message
			tokenSpace[4] = tokenSpace[4].substring(1);
			return tokenSpace;
		} else if (tokenSpace[2].equals("USERSTATE")) {
			return tokenSpace;
		}
		return null;
	}
	
	/**
	 * splits the input string with the delimSemicolon regex
	 * @param input
	 * @return String[]
	 */
	private String[] splitSemicolon(String input) {
		return input.split(delimSemicolon);
	}
	
	/**
	 * checks the user for things different badges / unserstatus
	 * (broadcaster, mod, etc ...)
	 * @param messageProperties
	 */
	private void usercheck(Properties messageProperties) {
		//is broadcaster or mod
		if (messageProperties.getProperty("@badges").contains("broadcaster") || messageProperties.getProperty("mod").equals("1")) {
			botMain.setBotModstate(true);
		} else {
			botMain.setBotModstate(false);
		}
		System.out.println("Updated modstate. Is mod / broadcaster: " + botMain.getBotModstate());
	}
}
