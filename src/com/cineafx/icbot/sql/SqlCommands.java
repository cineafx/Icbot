package com.cineafx.icbot.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class SqlCommands extends SqlMain {

	public SqlCommands(String servername, String username, String password, String dbname) {
		super(servername, username, password, dbname);

	}

	/**
	 * gets the command currently applying<br>
	 * channel specific commands > global commands<br>
	 * 
	 * @param command
	 * @param channel
	 * @return String[]
	 * <ol start = "0">
	 *  <li>command ID </li>
	 * 	<li>response</li>
	 * 	<li>userlevel</li>
	 * 	<li>timeout</li>
	 * 	<li>timesUsed</li>
	 * </ol>
	 */
	public String[] getCommand(String command, String channel) {
		try {
			
			//If channel starts with '#' cut it off
			if (channel.startsWith("#")) {
 				channel = channel.substring(1, channel.length());
			}
			
			//TODO: write explanation
			String statement = "SELECT DISTINCT commands.ID, response, userlevel, timeout, timesUsed "
					+ "FROM commands, channels "
					+ "WHERE command = ? "
					+ "AND (channel IS NULL OR channels.ID = commands.channel AND channels.channelName = ? ) "
					+ "ORDER BY channel DESC, ID DESC;";

			ResultSet rs = super.query(statement, command, channel);
			if (rs.first()) {
				//create returnArray
				String[] returnArray = new String[5];
				//fill array with content of row
				for (int i = 0; i < 5; i++) {
					returnArray[i] = rs.getString(i+1);
				}
				return returnArray;
			} else {
				//if not available "ignore message"
				return null;
			}

		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		return null;	
	}

	/**
	 * Increase timesUsed by 1 for id
	 * @param id
	 */
	public void updateTimesUsed(String id) {
		this.queryDDL("UPDATE commands "
				+ "SET timesUsed = timesUsed + 1 "
				+ "WHERE ID = '" + id + "';");
	}

}
