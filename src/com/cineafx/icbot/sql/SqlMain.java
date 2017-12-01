package com.cineafx.icbot.sql;

import java.sql.*;

public class SqlMain {
	private String dbUrl;
	private String user;
	private String pass;
	private String dbname;

	private Connection conn;
	private Statement stmt;

	public SqlMain(String servername, String username, String password, String dbname) {
		this.dbUrl  = "jdbc:mysql://" + servername + "/" + dbname;
		this.user = username;
		this.pass = password;
		this.dbname = dbname;

		try{
			//Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			// Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(dbUrl,user,pass);

			//Create statement
			stmt = conn.createStatement();

			System.out.println("Connected!");
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}

	}

	/**
	 * Default query method
	 * 
	 * @param statement
	 * @return ResultSet
	 */
	protected ResultSet query(String statement){
		try {
			//return query answer
			return stmt.executeQuery(statement);
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		return null;
	}

	//TODO: test this at some point ... please
	/**
	 * Retrieves full row depending on row index (starting at 0)
	 * 
	 * @param table
	 * @param row
	 * @return String[]
	 */
	public String[] getRow(String table, int row) {
		try {
			//returns amount of columns
			String countStatement = "SELECT count(*) "
					+ "FROM INFORMATION_SCHEMA.COLUMNS "
					+ "WHERE table_schema = '" + this.dbname + "' "
					+ "AND table_name = '" + table + "';";
			ResultSet rsCount = this.query(countStatement);
			rsCount.next();
			//get first (and only) result
			int columnAmount = rsCount.getInt(1);

			//create main query and ResultSet
			String statement = "SELECT * FROM " + table +";";
			ResultSet rs = this.query(statement);

			//create returnArray
			String[] returnArray = new String[columnAmount];
			rs.next();
			//fill array with content of row
			for (int i = 0; i < columnAmount; i++) {
				returnArray[i] = rs.getString(i);
			}
			return returnArray;
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
	 * Get a full column from one attribute
	 * @param table
	 * @param column
	 * @return String[]
	 */
	public String[] getColumn(String table, String column) {
		try {
			//return amount of rows
			String countStatement = "SELECT count(" + column + ") FROM " + table + ";";
			ResultSet rsCount = this.query(countStatement);
			rsCount.next();
			int rowAmount = rsCount.getInt(1);

			//create main query and ResultSet
			String statement = "SELECT " + column + " FROM " + table +";";
			ResultSet rs = this.query(statement);


			//create a String array with the size of the amount of returned rows
			String[] returnArray = new String[rowAmount];
			int i = 0;
			//while there is a new row add it to the array
			while (rs.next()) {
				returnArray[i] = rs.getString(column);
				i++;
			}
			return returnArray;
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
	 * get the current channels from the database
	 * 
	 * @return String[]
	 */
	public String[] getChannels() {

		//query from the table channels the attribute channelName
		String[] channels = this.getColumn("channels", "channelName");

		//if channelname doesn't start with # add one
		for (int i = 0; i < channels.length; i++) {
			if (!channels[i].startsWith("#")) {
				channels[i] = "#" + channels[i];
			}
		}		

		return channels;
	}
}
