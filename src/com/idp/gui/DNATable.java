package com.idp.gui;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class DNATable {

	private final SimpleIntegerProperty id;
	private final SimpleStringProperty sequence;

	public DNATable(Integer id, String sequence) {
		super();
		this.id = new SimpleIntegerProperty(id);
		this.sequence = new SimpleStringProperty(sequence);
	
	}

	public Integer getId() {
		return id.get();
	}
	
	public String getSequence() {
		return sequence.get();
	}
	
	
	
}
