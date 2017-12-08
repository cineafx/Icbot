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
	 * 	<li>response</li>
	 * 	<li>userlevel</li>
	 * 	<li>timeout</li>
	 *  <li>lastUsed</li>
	 * 	<li>timesUsed</li>
	 * </ol>
	 */
	public String[] getCommand(String command, String channel) {
		try {
			
			//TODO: fix / test the order of commands
			//TODO: write explanation
			String statement = "SELECT ID, response, userlevel, timeout, timesUsed "
					+ "FROM commands "
					+ "WHERE command = ? "
					+ "AND (channel = '" + channel + "' OR channel IS NULL)"
					+ "ORDER BY channel DESC, ID DESC;";

			ResultSet rs = super.query(statement, command);

			if (rs.next()) {
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
