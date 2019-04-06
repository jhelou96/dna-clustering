package com.idp.clustering;
import java.util.ArrayList;

public class Cluster {
	
	private ArrayList<Cluster> ClustChildren = new ArrayList<Cluster>();
	private String DNA;
	private double similarity;
	
	//Cluster can be created by giving it a DNA sequence
	public Cluster(String DNA){
		this.DNA = DNA;
	}
	
	//Cluster can be created by giving it cluster child
	public Cluster(Cluster c1, double similarity){
		
		this.ClustChildren.add(c1);
		this.similarity = similarity;
	}
	
	//Cluster can be created by giving it 2 clusters as children
	public Cluster(Cluster c1, Cluster c2, double similarity){
		
		this.ClustChildren.add(c1);
		this.ClustChildren.add(c2);
		this.similarity = similarity;
		
	}

	public ArrayList<Cluster> getClustChildren() {
		return ClustChildren;
	}
	
	public boolean hasChildren(Cluster c) {
		if(c.ClustChildren.isEmpty())
			return false;
		else
			return true;
	}
	
	public String getDNA() {
		return DNA;
	}
	
	public double getSimilarity() {
		return similarity;
	}
}