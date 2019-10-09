package blocks;

import java.util.ArrayList;
import java.util.HashSet;

import application.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import wikiAPI.API;

//MVC classification: C

/*
 * Blocklist is one of the key classes in this program, doing the following:
 * 1. Stores the sequence of blocks in ArrayList<Block> blockList.
 * 2. Performs searching behavior
 */

public class BlockList{
	private final ArrayList<Block> blockList = new ArrayList<Block>();
	
	private Pane pane;
	private Pane editPane;
	private StringProperty wikiLang;
	
	public BlockList() {
		wikiLang = new SimpleStringProperty();
		wikiLang.setValue("en");
	}
	
	public void bindToPane(Pane pane) {
		this.pane = pane;
	}
	
	public void bindEditPane(Pane pane) {
		this.editPane = pane;
	}
	
	public String[] start(String title) throws InterruptedException {
		return start(new String[] {title});
	}
	
	public String[] start(String[] titles) throws InterruptedException {
		HashSet<String> titlesList = new HashSet<String>();
		for (String title : titles) if (!title.isBlank()) titlesList.add(title);
		titles = titlesList.toArray(new String[0]);
		
		API.setWikiLang(wikiLang.getValue());
		int i = 0;
		int numBlocks = blockList.size();
		MainController.getLoadingScreen().restart();
		for (Block block : blockList) {
			System.out.println("Starting "+block.getClass().getSimpleName());
			MainController.getLoadingScreen().setStatus(titles.length, block.getClass().getSimpleName(), i, numBlocks);
			titles = block.handle(titles);
			Thread.currentThread().sleep(1000);
			i++;
		}
		return titles;
	}
	
	public void add(Block block) {
		blockList.add(block);
		edit(block);
		updatePane();
	}
	
	public void add(int index, Block block) {
		blockList.add(index, block);
		edit(block);
		updatePane();
	}
	
	public void remove(Block block) {
		blockList.remove(block);
	}
	
	public void edit(Block block) {
		clearEdit();
		EditableAttribute[] attrs = block.getAttributes();
		
		for (EditableAttribute attr : attrs) {
			editPane.getChildren().add(attr.getEditPane());
		}
		
		Button delete = new Button("Delete block");
		delete.setOnAction((ActionEvent e)->{
			clearEdit();
			delete(block);
		});
		delete.setStyle("-fx-background-color: #ff5400; -fx-font-size: 20");
		editPane.getChildren().add(delete);
	}
	
	public void delete(Block block) {
		blockList.remove(block);
		updatePane();
	}
	
	public void clearEdit() {
		editPane.getChildren().clear();
	}
	
	public void moveLeft(Block block) {
		int pos = blockList.indexOf(block);
		if (pos == 0) return;
		blockList.remove(block);
		add(pos-1, block);
	}
	
	public void moveRight(Block block) {
		int pos = blockList.indexOf(block);
		if (pos == blockList.size()-1) return;
		blockList.remove(block);
		add(pos+1, block);
	}
	
	private void updatePane() {
		pane.getChildren().clear();
		for (Block b : blockList) {
			BlockPane blockPane = new BlockPane(this, b);
			pane.getChildren().add(blockPane.getPane(wikiLang.getValue()));
			pane.getChildren().add(new Separator(Orientation.VERTICAL));
		}
		MainController.getI18nUpdater().update();
	}

	public Block set(int index, Block block) {
		Block oldBlock = blockList.get(index);
		blockList.set(index, block);
		return oldBlock;
	}

	public Block get(int index) {
		return blockList.get(index);
	}
	
	public ArrayList<Block> getBlockList() {
		return blockList;
	}
	
	public void setBlockList(ArrayList<Block> blocks) {
		for (Block b : blockList) remove(b);
		blockList.addAll(blocks);
		clearEdit();
		updatePane();
	}
	

	public int size() {
		return blockList.size();
	}

	public StringProperty getWikiLang() {
		return wikiLang;
	}

	public void setWikiLang(StringProperty wikiLang) {
		this.wikiLang = wikiLang;
	}
	
	public String getWikiURL() {
		return API.getWikiURL();
	}
}
