package com.cineafx.icbot.bot.messageHandler;

import java.util.Arrays;
import java.util.Properties;

public class MessageHandlerMain {
	
private static String delimSpace = "[ ]";
private static String delimSemicolon = "[;]";
private static String[] propertiesNames = {"user-name","messagetype", "channel","message"};

	public MessageHandlerMain() {
		
		
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
		
		
		//gets the content of the first tokenSpace as an array 
		String[] tokenSemicolon = this.splitSemicolon(tokenSpace[0]);
		
		//removes the first element of the tokenSpace array
		tokenSpace = Arrays.copyOfRange(tokenSpace, 1, tokenSpace.length);
		
		
		//set the properties for username, messagetype, channelname, message
		for (int i = 0; i < 4; i++) {
			messageProperties.setProperty(propertiesNames[i], tokenSpace[i]);
		}
		
		//set the properties for all other available properties
		for (int i = 4; i < tokenSemicolon.length; i++) {
			//split the parameter into name and value
			String[] propertieToken = tokenSemicolon[i].split("[=]", 2);
			//add the properties 
			messageProperties.setProperty(propertieToken[0], propertieToken[1]);
		}
		
		System.out.println(messageProperties);
		
		
		return messageProperties;
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
		if (tokenSpace[2].equals("PRIVMSG")) {
			
			//only keep everything between the ":" and the "!" to get the clean username
			tokenSpace[1] = tokenSpace[1].substring(1, tokenSpace[1].indexOf('!'));
			
			//remove the leading ":" from the message
			tokenSpace[4] = tokenSpace[4].substring(1);
			
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
}
