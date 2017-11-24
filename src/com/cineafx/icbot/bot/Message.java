package com.cineafx.icbot.bot;

import java.util.Properties;

public class Message {
	private Properties messageProperties;

	/**
	 * Create a set of properties to be read with this class
	 * property names can be found here: {@link com.cineafx.icbot.bot.messageHandler.MessageHandlerMain#getMessageProperties(String)}
	 * 
	 * @param messageProperties
	 */

	public Message(Properties messageProperties) {
		this.messageProperties = messageProperties;
	}


	/**
	 * Returns whether a value of the current property name is equals to the value to check for<br>
	 * property names can be found here: {@link com.cineafx.icbot.bot.messageHandler.MessageHandlerMain#getMessageProperties(String)}
	 * 
	 * @param propertyName
	 * @param valueToCheckFor
	 * @return boolean
	 */
	public boolean checkProperty(String propertyName, String valueToCheckFor) {
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
	public boolean checkProperty(String propertyName, String[] valuesToCheckFor) {
		for (String string : valuesToCheckFor) {
			if (messageProperties.getProperty(propertyName).equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the User name from messageProperties
	 * @return user name
	 */
	public String getUserName() {
		return messageProperties.getProperty("user-name");
	}

	/**
	 * Returns the Messagetype from messageProperties
	 * @return messagetype
	 */
	public String getMessagetype() {
		return messageProperties.getProperty("messagetype");
	}

	/**
	 * Returns the channel name from messageProperties
	 * @return channel name
	 */
	public String getChannel() {
		return messageProperties.getProperty("channel");
	}

	/**
	 * Returns the message from messageProperties
	 * @return message
	 */
	public String getMessage() {
		return messageProperties.getProperty("message");
	}

	/**
	 * Returns the badges from messageProperties
	 * @return badges
	 */	
	public String getBadges() {
		return messageProperties.getProperty("@badges");
	}

	/**
	 * Returns the color from messageProperties
	 * @return color
	 */
	public String getColor() {
		return messageProperties.getProperty("color");
	}

	/**
	 * Returns the display name from messageProperties
	 * @return display name
	 */
	public String getDisplayName() {
		return messageProperties.getProperty("display-name");
	}

	/**
	 * Returns the emotes from messageProperties
	 * @return emotes
	 */
	public String getEmotes() {
		return messageProperties.getProperty("emotes");
	}

	/**
	 * Returns the id from messageProperties
	 * @return id
	 */
	public String getId() {
		return messageProperties.getProperty("id");
	}

	/**
	 * Returns the mod status from messageProperties
	 * @return mod status
	 */
	public String getMod() {
		return messageProperties.getProperty("mod");
	}

	/**
	 * Returns the room id from messageProperties
	 * @return room id
	 */
	public String getRoomId() {
		return messageProperties.getProperty("room-id");
	}

	/**
	 * Returns the subscriber status from messageProperties
	 * @return subscriber status
	 */
	public String getSubscriber() {
		return messageProperties.getProperty("subscriber");
	}

	/**
	 * Returns the tmi sent ts from messageProperties
	 * @return tmi sent ts
	 */
	public String getTmiSentTs() {
		return messageProperties.getProperty("tmi-sent-ts");
	}

	/**
	 * Returns the turbo status from messageProperties
	 * @return turbo status
	 */
	public String getTurbo() {
		return messageProperties.getProperty("turbo");
	}

	/**
	 * Returns the user id from messageProperties
	 * @return user id
	 */
	public String getUserId() {
		return messageProperties.getProperty("user-id");
	}

	/**
	 * Returns the user type from messageProperties
	 * @return user type
	 */
	public String getUserType() {
		return messageProperties.getProperty("user-type");
	}
}