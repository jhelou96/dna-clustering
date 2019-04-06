package com.idp.gui;



import java.util.ArrayList;
import java.util.Random;


public class DNASequenceGenerator {
	
	private ArrayList<String> AllDNA;
	
	public DNASequenceGenerator(int a, int max){
		
		String Ambnucleo = "AGCTRYKMSWBDHVN";
		ArrayList<String> array = new ArrayList<>();
		
		String sequence ="";
	
		Random randomgen = new Random();
		Random rnd = new Random();
		int c =  randomgen.nextInt(100);
		
		for(int i=0; i < a; i++){
			
			for (int k = 0; k < rnd.nextInt(max)+1 ; k++) {
				sequence += Ambnucleo.charAt(rnd.nextInt(Ambnucleo.length()));
				for(int j=0; j <array.size(); j++){   		// This part was added to make sure that no 2 DNAs are equal
					if(array.get(j)==sequence){
						sequence="";
						k--;
					}
				}
			}
			
			array.add(sequence);
			sequence="";
		
		}
		
		this.AllDNA = array;
	}
	
	
	public ArrayList<String> getDNAList() {
		return AllDNA;
	}
	
	
	
	
}

