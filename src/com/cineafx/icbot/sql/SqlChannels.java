package com.cineafx.icbot.sql;

public final class SqlChannels extends SqlMain {

	public SqlChannels(String servername, String username, String password, String dbname) {
		super(servername, username, password, dbname);
	}

	/**
	 * get the current channels from the database
	 * 
	 * @return String[]
	 */
	public String[] getChannels() {

		//query from the table channels the attribute channelName
		String[] channels = super.getColumn("channels", "channelName");

		//if channelname doesn't start with # add one
		for (int i = 0; i < channels.length; i++) {
			if (!channels[i].startsWith("#")) {
				channels[i] = "#" + channels[i];
			}
		}		

		return channels;
	}

}
