package blocks;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

//MVC classification: C
//Controller class for BlockPane.fxml

public class BlockPaneController implements Initializable{
	@FXML
	private Label blockTypeLBL;
	@FXML
	private BorderPane rootPANE;
	private BlockList containingList;
	private Block block;

	public void setContainingList(BlockList containingList) {
		this.containingList = containingList;
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public void setName(String name) {
		blockTypeLBL.setText(name);
	}
	
	public StringProperty getStringProperty() {
		return blockTypeLBL.textProperty();
	}
	
	// Event Listener on Button.onAction
	@FXML
	public void handleMoveRight(ActionEvent event) {
		containingList.moveRight(block);
	}
	// Event Listener on Button.onAction
	@FXML
	public void handleMoveLeft(ActionEvent event) {
		containingList.moveLeft(block);
	}
	// Event Listener on Button.onAction
	@FXML
	public void handleEdit(ActionEvent event) {
		containingList.edit(block);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		rootPANE.setStyle("-fx-background: #34383B");
	}
}
