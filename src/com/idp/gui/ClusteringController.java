package com.idp.gui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.idp.clustering.DB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ClusteringController implements Initializable {
	private DB db = new DB();
	@FXML private TableView<DNATable> table;
	@FXML private TableColumn<DNATable,String> sequence;
	@FXML private TableColumn<DNATable,Integer> id;
	@FXML private Button deleteButton;
	@FXML private Button deleteAllButton;
	@FXML private Button runAlgoButton;
	
	public ObservableList<DNATable> list = FXCollections.observableArrayList();
	
	public ClusteringController() throws SQLException {
		
		ArrayList<String> DNASequences = db.retrieveDNAs();
		
		for(int i = 0; i < DNASequences.size(); i++) {
			DNATable d = new DNATable(i, DNASequences.get(i));
			list.add(d);
		}
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		id.setCellValueFactory(new PropertyValueFactory<DNATable,Integer>("id"));
		sequence.setCellValueFactory(new PropertyValueFactory<DNATable,String>("sequence"));
		table.setItems(list);
		
		if(table.getItems().isEmpty()) {
			deleteButton.setDisable(true);
			deleteAllButton.setDisable(true);
		}
		
		if(table.getItems().size() > 0)
			runAlgoButton.setDisable(false);
		else
			runAlgoButton.setDisable(true);
	}
	
	@FXML private TextField txt = new TextField();
	@FXML private Button bt = new Button();
	String ValidSequence = "AGCTRYKMSWBDHVN";

	public void insert(ActionEvent e) throws SQLException {
		//We verify if DNA sequence is valid
		Boolean verification = true;
		
		if(txt.getText().length() == 0)
			verification = false;
		
		for(int i =0; i < txt.getText().length();i++){
			if(ValidSequence.indexOf(txt.getText().charAt(i))<0 && verification){
				Alert a = new Alert(AlertType.INFORMATION);
		        a.setTitle("Invalid sequence");
		        a.setHeaderText("Sequence contains invalid nucleotid !");
		        a.setResizable(true);
		        String content = String.format("Please note that the acceptable nucleotides and ambiguities are: AGCTRYKMSWBDHVN.");
		        a.setContentText(content);
		        a.showAndWait();
		        verification = false;
		     }
		}	   
		
		if(db.checkIfDNAExists(txt.getText())) {
			Alert a = new Alert(AlertType.INFORMATION);
	        a.setTitle("Sequence already exists");
	        a.setHeaderText("DNA sequence already exists !");
	        a.setResizable(true);
	        String content = String.format("Please note that there can be no two same DNA sequences in the database.");
	        a.setContentText(content);
	        a.showAndWait();
	        verification = false;
		}
		
		if(verification) {
			db.InsertDNA(txt.getText());
			txt.setText("");
			//We remove and retrieve back all the DNA sequences after insertion to refresh table
			 list.removeAll(list);
			 ArrayList<String> DNASequences = db.retrieveDNAs();
				
			for(int i = 0; i < DNASequences.size(); i++) {
				DNATable d = new DNATable(i, DNASequences.get(i));
				list.add(d);
			}
			
			deleteButton.setDisable(false);
			deleteAllButton.setDisable(false);
			
			if(table.getItems().size() > 0)
				runAlgoButton.setDisable(false);
			else
				runAlgoButton.setDisable(true);
		}
	}
	
	public void delete(ActionEvent event) throws SQLException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Wait");
		alert.setHeaderText("Are you sure you want to delete this sequence ?");
		alert.setContentText("This action cannot be undone !");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			db.deleteDNA(table.getSelectionModel().getSelectedItem().getSequence());
			
			//We remove and retrieve back all the DNA sequences after insertion to refresh table
			list.removeAll(list);
			ArrayList<String> DNASequences = db.retrieveDNAs();
				
			for(int i = 0; i < DNASequences.size(); i++) {
				DNATable d = new DNATable(i, DNASequences.get(i));
				list.add(d);
			}
			
			if(table.getItems().isEmpty()) {
				deleteButton.setDisable(true);
				deleteAllButton.setDisable(true);
			}
			
			if(table.getItems().size() > 0)
				runAlgoButton.setDisable(false);
			else
				runAlgoButton.setDisable(true);
		}
	}
	
	public void deleteAll(ActionEvent event) throws SQLException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Wait");
		alert.setHeaderText("Are you sure you want to delete this sequence ?");
		alert.setContentText("This action cannot be undone !");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			db.deleteDNA();
			
			//We remove and retrieve back all the DNA sequences after insertion to refresh table
			list.removeAll(list);
			ArrayList<String> DNASequences = db.retrieveDNAs();
			
			for(int i = 0; i < DNASequences.size(); i++) {
				DNATable d = new DNATable(i, DNASequences.get(i));
				list.add(d);
			}
			
			deleteButton.setDisable(true);
			deleteAllButton.setDisable(true);
			runAlgoButton.setDisable(true);
		}
	}
	
	@FXML private TextField txtnumbSequence = new TextField();
	@FXML private TextField max = new TextField();
	@FXML private Button AutoFill = new Button();
	
	public void Autoinsert(ActionEvent e) throws SQLException {
		//We verify if DNA sequence is valid
		Boolean verification = true;

		if(txtnumbSequence.getText().length() == 0)
			verification = false;
		if(max.getText().length() == 0)
			verification = false;
		
		if(verification) {
			int a = Integer.parseInt(txtnumbSequence.getText());
			int b = Integer.parseInt(max.getText());
			DNASequenceGenerator dgen = new DNASequenceGenerator(a,b);
			for(int i=0 ; i<dgen.getDNAList().size(); i++) {
			db.InsertDNA(dgen.getDNAList().get(i));
			}
			txtnumbSequence.setText("");
			//We remove and retrieve back all the DNA sequences after insertion to refresh table
			 list.removeAll(list);
			 ArrayList<String> DNASequences = db.retrieveDNAs();
				
			for(int i = 0; i < DNASequences.size(); i++) {
				DNATable d = new DNATable(i, DNASequences.get(i));
				list.add(d);
			}
			
			deleteButton.setDisable(false);
			deleteAllButton.setDisable(false);
			
			if(table.getItems().size() > 0)
				runAlgoButton.setDisable(false);
			else
				runAlgoButton.setDisable(true);
		}
	}
	
	public void switchToResult(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Result.fxml"));
		Scene scene = new Scene(root);
		Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}