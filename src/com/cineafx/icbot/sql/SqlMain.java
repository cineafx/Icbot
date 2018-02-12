package com.cineafx.icbot.sql;

import java.sql.*;

public class SqlMain implements Runnable {
	private String dbUrl;
	private String user;
	private String pass;
	private String dbname;

	private Connection conn;

	public SqlMain(String servername, String username, String password, String dbname) {
		this.dbUrl  = "jdbc:mysql://" + servername + "/" + dbname;
		this.user = username;
		this.pass = password;
		this.dbname = dbname;

		this.connectDB();
		new Thread(this).start();
	}
	
	private void connectDB() {
		try{
			//Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			// Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(dbUrl,user,pass);

			System.out.println("Connected!");
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			this.connectDB();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes a select query<br>
	 * If no params are given a default query will be done<br>
	 * if params are given a prepared statement will be included
	 * 
	 * @param statement
	 * @param params
	 * @return ResultSet
	 */
	protected ResultSet query(String statement, String... params) {
		if (params != null) {
			try {
				//Create statement
				PreparedStatement stmt = conn.prepareStatement(statement);
				for (int i = 0; i < params.length; i++) {
					stmt.setString(i+1, params[i]);
				}
				return stmt.executeQuery();
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				this.connectDB();
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}
		} else {
			try {
				//Create statement
				Statement stmt = conn.createStatement();
				return stmt.executeQuery(statement);
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				this.connectDB();
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}		
		}
	
		return null;
	}
	
	/**
	 * Executes a DDL query<br>
	 * If no params are given a default query will be done<br>
	 * if params are given a prepared statement will be included
	 * 
	 * @param statement
	 * @param params
	 * @return ResultSet
	 */
	protected void queryDDL(String statement, String... params) {
		if (params != null) {
			try {
				//Create statement
				PreparedStatement stmt = conn.prepareStatement(statement);
				for (int i = 0; i < params.length; i++) {
					stmt.setString(i+1, params[i]);
				}
				stmt.executeUpdate();
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				this.connectDB();
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}
		} else {
			try {
				//Create statement
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(statement);
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				this.connectDB();
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * does a DDL query without PreparedStatement
	 * @param statement
	 */
	protected void queryDDL(String statement) {
		try {
			//Create statement
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(statement);
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			this.connectDB();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}		
	}

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
					+ "WHERE table_schema = ? "
					+ "AND table_name = ?;";
			ResultSet rsCount = this.query(countStatement, this.dbname, table);
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
				returnArray[i] = rs.getString(i+1);
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
	 * Get a full column from one attribute<br>
	 * <b>DOES NOT USE PREPARED STATEMENTS!!!<br>
	 * DO NOT LET THE USER DO ANY OF THESE INPUTS</b><br>
	 * 
	 * @param table
	 * @param column
	 * @return String[]
	 */
	public String[] getColumn(String table, String column) {
		try {
			//return amount of rows
			String countStatement = "SELECT COUNT(" + column + ") FROM " + table + ";";
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
	
	public void run() {
		while (true) {
			try{
				ResultSet rs = this.query("SELECT CURRENT_TIME;");
				rs.first();
				System.out.println("MYSQL ping send and received current time: " + rs.getTime(1));
				
				//wait 1 hour
				Thread.sleep(3600000);
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
			}catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
			}
		}
		
	}

}
