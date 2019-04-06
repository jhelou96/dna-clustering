package com.idp.clustering;
import java.sql.*;
import java.util.ArrayList;

public class DB {
	 private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	 private final String DB_URL = "jdbc:mysql://localhost/idp";
	 private final String USER = "root";
	 private final String PASS = "";
	 private Connection conn;
   
   public DB() {
   		try {
   			//STEP 1: Register JDBC driver
   			Class.forName(JDBC_DRIVER);

   			//STEP 2: Open a connection
   			conn = DriverManager.getConnection(DB_URL,USER,PASS);
   		} 	catch(SQLException se){
		   //Handle errors for JDBC
		   se.printStackTrace();
	   	} 	catch(Exception e){
		   //Handle errors for Class.forName
		   e.printStackTrace();
	   	} 
	}
   
   public ArrayList<String> retrieveDNAs() throws SQLException {
	   //STEP 3: Execute a query
		Statement stmt = null;
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT * FROM dnasequences";
		ResultSet rs = stmt.executeQuery(sql);
		
		//We save DNA sequences retrieved in an arrayList
		ArrayList<String> DNASequences = new ArrayList<>();
		
		//STEP 4: Extract data from result set
		while(rs.next()){
		    //Retrieve by column name
		    DNASequences.add(rs.getString("DNASequence"));
	 	}
		
		return DNASequences;
   	}
   
   public boolean checkIfDNAExists(String DNASequence) throws SQLException {
	   //STEP 3: Execute a query
		Statement stmt = null;
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT * FROM dnasequences WHERE DNASequence = '" + DNASequence + "'";
		ResultSet rs = stmt.executeQuery(sql);
		
		int count = 0;
		
		while(rs.next())
			count++;
		
		if(count > 0)
			return true;
		
		return false;
   	}
   
   public void InsertDNA(String dnasequence) throws SQLException {
		// STEP 5: Insert query
		Statement stmt = null;
	    stmt = conn.createStatement();
	    stmt.executeUpdate("INSERT INTO dnasequences(DNASequence) VALUES('" + dnasequence + "')");
	}
	
	public void deleteDNA(String dnasequence) throws SQLException {
		// STEP 5: delete query
		Statement stmt = null;
	    stmt = conn.createStatement();
	    stmt.executeUpdate("DELETE FROM dnasequences WHERE DNASequence = '" + dnasequence + "'");
	}
	
	public void deleteDNA() throws SQLException {
		// STEP 5: delete query
		Statement stmt = null;
	    stmt = conn.createStatement();
	    stmt.executeUpdate("DELETE FROM dnasequences");
	}
}