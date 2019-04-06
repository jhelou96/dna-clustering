package com.idp.clustering;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Clustering {
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private ArrayList<String> simTables = new ArrayList<String>();
	
	public Clustering() throws SQLException {
		//We get the DNA sequences from the DB
		DB db = new DB();
		ArrayList<String> DNASequences = db.retrieveDNAs();
		
		//DNA sequences are retrieved as strings
		//We separate each sequence as an invidividual cluster
		for(int i = 0; i < DNASequences.size(); i++) {
			Cluster c = new Cluster(DNASequences.get(i));
			clusters.add(c);
		}
		
		
		
	}
	
	public void runAlgorithm() {
		double[][] initialTable = this.buildSimTable(); //We build the similarity table
		
		while(initialTable.length > 1){
			//We format the table and save it in an array
			this.formatTable(initialTable);
			
			int a = 0,b=0;
			double max = 0.0;
			
			//We get the max similarity between 2 different clusters
			for(int i=0; i< initialTable.length ; i++){
				for(int j = 0; j < initialTable.length && j < i; j++) { // j < i because we are only interested in the part under the diagonal
					if(initialTable[i][j] > max && initialTable[i][j] != 1) {
						max = initialTable[i][j];
						
						//a will be used as the index for the row and column that will contain the new cluster that will result after merging the most 2 similar clusters in the table
						a = Math.min(i, j); //a is the minimum index between i and j
						
						//b will be used as the index for the row and column that will be deleted after merging the 2 most similar clusters
						b = Math.max(i, j); //b is the maximum index between i and j
					}
				}
			}
			
			//We create a new table
			double[][] newTable = new double[initialTable.length-1][initialTable.length-1];
			
			//We populate the new table by first copy pasting the values we're interested in in the previous table
			for(int i = 0; i < newTable.length; i++) {
				for(int j = 0; j < newTable.length; j++) {
					if(i < b) {
						//If row and column are below b, we copy paste similarity as it is
						if(j < b)
							newTable[i][j] = initialTable[i][j];
						//If row is below b but column is larger or equal to b, we shift index by one because we are not interested in column b since we want to delete it
						else
							newTable[i][j] = initialTable[i][j+1];
					} else if(i >= b) {
						//if row is greater or equal to b and column less than b, we shift only row by one because we are not interested in row b since we want to delete it
						if(j < b)
							newTable[i][j] = initialTable[i+1][j];
						//If row and column are greater or equal to b, we shift both by one because we are not interested in row b and column b since we want to delete them
						else
							newTable[i][j] = initialTable[i+1][j+1];
					}
				}
			}
			
			//Now we have to compute the new similarities present in column a and row a since they represent a new cluster
			
			//We start with row a
			int j = 0;			
			while(j < newTable.length) {
				if(j == a)
					newTable[a][j] = 1; //If j = a, it means row = column therefore similarity = 1
				else {
					//If column is less than b, we take the average similarity between the 2 old clusters that were merged with cluster at column j
					if(j < b)
						newTable[a][j] = (initialTable[a][j] + initialTable[b][j])/2;
					//If column is greater or equal to b, we take the average similarity between the 2 old clusters that were merged with cluster at column j+1 because we deleted column b
					else
						newTable[a][j] = (initialTable[a][j+1] + initialTable[b][j+1])/2;
				}
				
				j++;
			}
			
			//We do the same but now for column a
			int i = 0;			
			while(i < newTable.length) {
				if(i == a)
					newTable[i][a] = 1;
				else {
					if(i < b)
						newTable[i][a] = (initialTable[i][a] + initialTable[i][b])/2;
					else
						newTable[i][a] = (initialTable[i+1][a] + initialTable[i+1][b])/2;
				}
				
				i++;
			}
			//We create a new cluster
			Cluster cluster = new Cluster(clusters.get(a),clusters.get(b), initialTable[a][b]);
			clusters.remove(b);
			clusters.set(a, cluster);
			
			//We set initial table = newTable to loop again with the newly created table
			initialTable = newTable;
			
		}
		
		//We format the table and save it in an array
		this.formatTable(initialTable);
	}
	
	//Builds a similarity table by using the algorithm written in project 1
	public double[][] buildSimTable() {
		double[][] table = new double[clusters.size()][clusters.size()];
		
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table.length; j++) {
				WFAlgo wf = new WFAlgo(clusters.get(i).getDNA(), clusters.get(j).getDNA());
				wf.buildTable();
				
				table[i][j] = wf.getSimilarity();
			}
		}
		
		return table;
	}
	
	public TreeItem buildDendrogram(Cluster cluster) {
		//We check if cluster has children
		DecimalFormat df = new DecimalFormat("0.00");
		if(!cluster.getClustChildren().isEmpty()) {
			TreeItem<String> item = new TreeItem<String> ("" + df.format(cluster.getSimilarity()), new ImageView(new Image(getClass().getResourceAsStream("../gui/root.png"))));            
            item.getChildren().add(buildDendrogram(cluster.getClustChildren().get(0)));
            
			//If cluster has more than 1 children, we add the other one (Clusters can have at max 2 children)
			if(cluster.getClustChildren().size() > 1) {
				 item.getChildren().add(buildDendrogram(cluster.getClustChildren().get(1)));
			}
			
			return item;
		} else {
			//If cluster has no children, then we create a new node and append to it the DNA sequence
			TreeItem<String> item = new TreeItem<String> (cluster.getDNA(), new ImageView(new Image(getClass().getResourceAsStream("../gui/leaf.png"))));  
			return item;
		}
	}
	
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}
	
	public void formatTable(double[][] table) {
		//Prints a 2D array as a table
		String formattedTable = "";
		DecimalFormat df = new DecimalFormat("0.00");
		for (int row = 0; row < table.length; ++row) {
			formattedTable += "|";
			
			for (int col = 0; col < table[row].length; ++col) {
				formattedTable += df.format(table[row][col]);
				formattedTable += "|";
			}

			formattedTable += "\n";
		} 
		
		//We add the formatted table into an array
		simTables.add(formattedTable);
	}
	
	public ArrayList<String> getSimTables() {												
		return simTables;
	}
	
	
}
