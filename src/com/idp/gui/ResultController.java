package com.idp.gui;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.idp.clustering.Cluster;
import com.idp.clustering.Clustering;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ResultController implements Initializable {
	@FXML
    private TextArea resultBox;
	@FXML
	private Button dendrogramButton;
	private Clustering clustering;
	
	@Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		try {
			clustering = new Clustering();
			
			long startTime = System.currentTimeMillis();
			clustering.runAlgorithm();
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			
			resultBox.appendText("----------------------------");
			resultBox.appendText("\n");
			resultBox.appendText("DNA CLUSTERING TOOL");
			resultBox.appendText("\n");
			resultBox.appendText("----------------------------");
			resultBox.appendText("\n\n");
			
			//We display the tables
			ArrayList<String> simTables = clustering.getSimTables();
			for(int i = 0; i < simTables.size(); i++) {
				resultBox.appendText(simTables.get(i));
				resultBox.appendText("\n");
			}
			
			resultBox.appendText("It took me " + totalTime + "ms to cluster your DNA sequences !");
			resultBox.appendText("\n");
			resultBox.appendText("--------------------------------------------------------------------");
			resultBox.appendText("\n");
			resultBox.appendText("You can now generate the dendrogram if you wish to !");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void drawDendrogram(ActionEvent event) {
		Cluster rootCluster = clustering.getClusters().get(0);      
        
        TreeItem<String> rootItem = clustering.buildDendrogram(rootCluster);
        rootItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("root.png"))));
        rootItem.setExpanded(true);


        TreeView<String> tree = new TreeView<String> (rootItem);        
        StackPane root = new StackPane();
        root.getChildren().add(tree);
        Stage stage = new Stage();
        stage.setTitle("Dendrogram");
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
	}
	
	public void switchToClustering(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Clustering.fxml"));
		Scene scene = new Scene(root);
		Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
