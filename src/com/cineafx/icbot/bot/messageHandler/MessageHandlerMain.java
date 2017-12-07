package com.cineafx.icbot.bot.messageHandler;

import java.util.Properties;

import com.cineafx.icbot.bot.BotMain;

public class MessageHandlerMain {

	//classes / objects
	private BotMain botMain;
	private PropertieHandler propertiehandler;
	private CommandHandler commandHandler;
	
	//other
	private Properties messageProperties = new Properties();
	private String returnMessage;

	public MessageHandlerMain(BotMain botMain) {
		this.botMain = botMain;
		propertiehandler = new PropertieHandler(botMain);
		commandHandler = new CommandHandler(botMain);
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public String handleMessage(String input){
		returnMessage = null;
		
		//generates messageProperties from in incoming line
		messageProperties = propertiehandler.getMessageProperties(input);

		
		if (messageProperties != null) {
			//create a message object which is easier to use than messageProperties.getProperty("X");
			//message = new Message(messageProperties);
			//Prints out #channel user-name: message
			//System.out.println(message.getChannel()+ " " + message.getUserName() + ": " + message.getMessage());

			/*
			if (message.getMessage().contains("i c ") || message.getMessage().endsWith("i c")) {
				send("miniW ");
			}
			 */
			if (returnMessage == null) {
				returnMessage = checkForPing(messageProperties);
			}
			if (returnMessage == null) {
				returnMessage = checkForShutdown(messageProperties);
			}
			
			//TODO: command handling
			returnMessage = commandHandler.checkForCommand(messageProperties);
			
			
			System.out.println(botMain.getChannelname() + " " + messageProperties.getProperty("user-name") + ": " + messageProperties.getProperty("message"));

		}
		
		
		return returnMessage; 
	}
	
	/**
	 * returns the appropriate message for the ping commands or NULL
	 * 
	 * @param property
	 * @return returnString
	 */
	private String checkForPing(Properties property) {
		String returnMessage = null;
		
		//ping command
			if (checkProperty(property,"message", new String[] {"!icping","!pingall"}) && checkProperty(property,"user-name", botMain.getAdmin())) {
				returnMessage = messageProperties.getProperty("user-name") + ", sure LuL";
			}
			
		return returnMessage;
	}
	
	/**
	 * returns the appropriate message for the quit commands or NULL
	 * 
	 * @param property
	 * @return returnString
	 */
	private String checkForShutdown(Properties property) {
		String returnMessage = null;
		
		//check for shutdown command
		if (checkProperty(property, "message", "!icquit") && checkProperty(property, "user-name", botMain.getAdmin())) {
			returnMessage = messageProperties.getProperty("user-name") + ", " + "Shutting down...";
			//set isRunning to false and "end" the queue which will terminate the program
			botMain.setRunning(false);
		}
		
		return returnMessage;
	}

	/**
	 * Returns whether a value of the current property name is equals to the value to check for<br>
	 * property names can be found here: {@link com.cineafx.icbot.bot.messageHandler.MessageHandlerMain#getMessageProperties(String)}
	 * 
	 * @param propertyName
	 * @param valueToCheckFor
	 * @return boolean
	 */
	public boolean checkProperty(Properties messageProperty, String propertyName, String valueToCheckFor) {
		return messageProperties.getProperty(propertyName).equals(valueToCheckFor);
	}

	/**
	 * Returns whether a value of the current property name is equals to the value to check for<br>
	 * property names can be found here: {@link com.cineafx.icbot.bot.messageHandler.MessageHandlerMain#getMessageProperties(String)}<br>
	 * Use the following code example to create nameless String arrays<pre>
	 * <code> checkProperty("propertyName", new String[] {"value1","value2"});</code></pre>
	 * 
	 * @param propertyName
	 * @param valuesToCheckFor
	 * @return boolean
	 */
	public boolean checkProperty(Properties messageProperty, String propertyName, String[] valuesToCheckFor) {
		for (String string : valuesToCheckFor) {
			if (messageProperties.getProperty(propertyName).equals(string)) {
				return true;
			}
		}
		return false;
	}

}
