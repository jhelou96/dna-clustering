package com.idp.clustering;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/*
	This class is used to build the SED and generate an Edit Script from 2 DNA sequences
*/

public class WFAlgo {
	private String DNA1, DNA2;
	private int delC = 1, insC = 1;
	private HashMap<Character, String> ambiguityHash;

	private String ambiguity = "RYKMSWBDHVN";
	private String nucleotide = "AGCT";

	private double[][] table; //Will contain the SED table
	private double SED;
	private double similarity;
	
	private NumberFormat NBFormatter;
	
	public WFAlgo(String DNASequence1, String DNASequence2) {
		DNA1 = DNASequence1;
		DNA2 = DNASequence2;
		
		// HashMap for ambiguities
		ambiguityHash = new HashMap<Character, String>();
		ambiguityHash.put('R', "GA");
		ambiguityHash.put('Y', "TC");
		ambiguityHash.put('K', "GT");
		ambiguityHash.put('M', "AC");
		ambiguityHash.put('S', "GC");
		ambiguityHash.put('W', "AT");
		ambiguityHash.put('B', "GTC");
		ambiguityHash.put('D', "GAT");
		ambiguityHash.put('H', "ACT");
		ambiguityHash.put('V', "GCA");
		ambiguityHash.put('N', "AGCT");

		table = new double[DNA1.length()+1][DNA2.length()+1];
		
		buildTable();
	}

	//Check price of update
	public int updateSimpleCost(char char1, char char2) {
		if (char1 == char2)
			return 0;
		else
			return 1;
	}

	//Get full update cost between 2 chars
	public double updateCost(char char1, char char2) {
		double cost = 0.0;
		
		String str1; // Ambiguity String 1
		String str2; // Ambiguity String 2

		// case of two ambiguities
		if (ambiguity.indexOf(char1) >= 0 && ambiguity.indexOf(char2) >= 0) {

			if (char1 == char2) // check if the characters are the same then no cost
				return cost;

			str1 = ambiguityHash.get(char1); //Get Ambiguity related to char1
			str2 = ambiguityHash.get(char2); //Get Ambiguity related to char2

			//Compute update cost in case char1 and char2 contain 2 ambiguities
			for (int i = 0; i < str1.length(); i++) {
				for (int j = 0; j < str2.length(); j++) {
					cost += ((double) 1 / str1.length()) * ((double) 1 / str2.length()) * (updateSimpleCost(str1.charAt(i), str2.charAt(j)));

				}
			}

		}

		else if (ambiguity.indexOf(char1) >= 0 && nucleotide.indexOf(char2) >= 0) { // case of char1 ambiguity
		
			if (char1 == char2) // check if the characters are the same then no cost
				return cost;

			str1 = ambiguityHash.get(char1); //Get Ambiguity related to char1

			//Compute update cost in case char1 contains 1 ambiguity
			for (int i = 0; i < str1.length(); i++) {
				cost += ((double) 1 / str1.length()) * (updateSimpleCost(str1.charAt(i), char2));
			}

		} else if (nucleotide.indexOf(char1) >= 0 && ambiguity.indexOf(char2) >= 0) { // case of char2 ambiguity

			if (char1 == char2) // check if the characters are the same then no cost
				return cost;

			str2 = ambiguityHash.get(char2); //Get Ambiguity related to char2

			//Compute update cost in case char2 contains 1 ambiguity
			for (int i = 0; i < str2.length(); i++) {
				cost += ((double) 1 / str2.length()) * (updateSimpleCost(str2.charAt(i), char1));
			}

		} else if (nucleotide.indexOf(char1) >= 0 && nucleotide.indexOf(char2) >= 0) { // case of two nucleotides
			
			//Compute cost in case of 0 ambiguity
			cost = updateSimpleCost(char1, char2);
			
		} else { // case of Invalid DNA Sequence
			JOptionPane.showMessageDialog(null, "Please provide valid DNA sequences !", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		return cost;

	}

	//Build the SED table using Wagner Fisher algorithm
	public void buildTable() {
		table[0][0] = 0;

		for (int i = 1; i <= DNA1.length(); i++)
			table[i][0] = table[i - 1][0] + delC;
		for (int j = 1; j <= DNA2.length(); j++)
			table[0][j] = table[0][j - 1] + insC;

		for (int i = 1; i <= DNA1.length(); i++) {
			for (int j = 1; j <= DNA2.length(); j++) {

				double insV = table[i][j - 1] + insC;
				double delV = table[i - 1][j] + delC;
				double updV = table[i - 1][j - 1] + updateCost(DNA1.charAt(i - 1), DNA2.charAt(j - 1));

				table[i][j] = Math.min(delV, Math.min(insV, updV));

			}
		}
		
		SED = table[DNA1.length()][DNA2.length()];
		similarity = 1 / (SED+1);
	}
	
	//Print the array in form of a table
	public String formatTable() {
		String formattedTable = "";
		
		for (int row = 0; row < table.length; ++row) {
			// printing data row
			formattedTable += "|";
			for (int col = 0; col < table[row].length; ++col) {
				formattedTable += NBFormatter.format(table[row][col]);
				formattedTable += "|";
			}

			formattedTable += "\n";
		}
		
		return formattedTable;
	}
	
	//GETTERS
	public double[][] getTable() {
		return table;
	}
	
	
	public double getSED() {
		return SED;
	}
	
	public double getSimilarity() {
		return similarity;
	}
	
	public String getDNASequence1() {
		return DNA1;
	}
	
	public String getDNASequence2() {
		return DNA2;
	}
}
