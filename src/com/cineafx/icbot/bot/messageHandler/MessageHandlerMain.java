package com.cineafx.icbot.bot.messageHandler;

import java.util.Arrays;
import java.util.Properties;

public class MessageHandlerMain {
	
private static String delimSpace = "[ ]";
private static String delimSemicolon = "[;]";
private static String[] propertiesNames = {"user","messagetype", "channel","message"};

	public MessageHandlerMain() {
		
		
	}
	
	/**
	 * Gives back the input stream split into the single properties:
	 * 
	 * Messagetype (PRIVMSG)
	 * Channel
	 * User
	 * Message
	 * 
	 * @badges=
	 * color=
	 * display-name=
	 * emotes=
	 * id=
	 * mod=
	 * room-id=
	 * subscriber=
	 * tmi-sent-ts=
	 * turbo=
	 * user-id=
	 * user-type=
	 * 
	 * @param input
	 * @return properties
	 */
	public Properties getMessageProperties(String input) {
		Properties properties = new Properties();
		
		
		String[] tokens = this.split(input);
		if (tokens == null) {
			return null;
		}
		//only keep everything between the ":" and the "!"
		tokens[0] = tokens[0].substring(1, tokens[0].indexOf('!'));
		
		//remove the leading ":" from the message
		tokens[3] = tokens[3].substring(1);
		
		//set the fir
		for (int i = 0; i < 4; i++) {
			properties.setProperty(propertiesNames[i], tokens[i]);
		}
		
		for (int i = 4; i < tokens.length; i++) {
			String[] propertieToken = tokens[i].split("[=]", 2);
			properties.setProperty(propertieToken[0], propertieToken[1]);
		}
		
		System.out.println(properties);
		
		
		return properties;
	}
	
	private String[] split(String input) {
		//Delimit the input array after spaces
		String[] tokenSpace = input.split(delimSpace, 5);
		//checks whether the incoming message is a PRIVMSG (normal chat message)
		if (tokenSpace[2].equals("PRIVMSG")) {

			//Delimit the first spot from the tokenSpace array by ;
			String[] tokenSemicolon = tokenSpace[0].split(delimSemicolon);
			
			//removes the first element of the array
			String[] tokenSpaceClean = Arrays.copyOfRange(tokenSpace, 1, tokenSpace.length);
			
			String[] tokens = this.appendStringArray(tokenSpaceClean, tokenSemicolon);
			
			return tokens;
		} else {
		return null;
		}
	}
	
	/**
	 * appends string array b to the end of string array a
	 * 
	 * @param a
	 * @param b
	 * @return combinded string array
	 */
	private String[] appendStringArray(String[] a, String[] b) {
	   int aLen = a.length;
	   int bLen = b.length;
	   
	   //creates new array from the length of array a length + array b length
	   String[] c = new String[aLen+bLen];
	   
	   //copy over the arrays in their specific location
	   System.arraycopy(a, 0, c, 0, aLen);
	   System.arraycopy(b, 0, c, aLen, bLen);
	   return c;
	}
}
