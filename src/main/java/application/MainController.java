package application;

import blocks.*;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//MVC classification: C

public class MainController implements Initializable{
	@FXML
	private HBox blockAddPANE;
	@FXML
	private HBox blocksPANE;
	@FXML
	private VBox editBlockPANE;
	@FXML
	private Button startSearchBTN;
	@FXML
	private GridPane resultGRID;
	private String[] results;
	@FXML
	private TextArea startPagesTXT;
	@FXML
	private HBox buttonHBOX;
	@FXML
	private Label addBlocksLBL;
	@FXML
	private Label addedBlocksLBL;
	@FXML
	private Label editBlocksLBL;
	@FXML
	private Label startPagesLBL;
	@FXML
	private Label editSearchTabLBL;
	@FXML
	private Label searchResultsTabLBL;
	@FXML
	private Label titleLBL;
	@FXML
	private Label instructionLBL;
	@FXML
	private Label viewerTitleLBL;
	@FXML
	private TabPane mainTAB;
	@FXML
	private TabPane viewerTAB;
	@FXML
	private Button exportResultsBTN;
	@FXML
	private Button importSearchBTN;
	@FXML
	private Button exportSearchBTN;
	@FXML
	private ImageView cornerIMG;
	
	private ComboBox<String> languageSelection;
	private BlockList blockList;
	private static Thread searchThread;
	private Tab loadingTab;
	private static LoadingScreen loadingScreen;
	static AtomicReference<String> state;
	private static InternationalStringUpdater i18nUpdater;
	
	public static final Class<? extends Block>[] blocks = new Class[] {
			TitleRegexBlock.class,
			TextRegexBlock.class,
			LinksBlock.class,
			IsLinkedByBlock.class,
			LinksToBlock.class,
			LinksFromBlock.class,
			IsInCategoryBlock.class,
			FromCategoryBlock.class
	};
	
	// Event Listener on Button[#startSearchBTN].onAction
	@FXML
	public void handleStartSearch(ActionEvent event) {
		System.out.println("Starting");
		if (startPagesTXT.getText().split("\n").length == 0) return;
		
		state = new AtomicReference<String>();
		resultGRID.getChildren().clear();
		searchThread = new Thread(()->{
			String[] pages = startPagesTXT.getText().split("\n");
			try {
				results = blockList.start(pages);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			int len = results.length;
			System.out.println(len);
			AtomicInteger pos = new AtomicInteger(0);
			for (; pos.get()<len;) {
				int i = pos.getAndIncrement();
				if (i < 1000)
					Platform.runLater(()->{
						resultGRID.add(genLabelFromString(results[i]), 0, i+1);
					});
			}
			if (len > 1001) {
				Platform.runLater(()->{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("Not all results will be displayed.\nExport to view the rest.");
					alert.setContentText("To prevent a laggy UI, only the first 1000 results are displayed.\nTo view the rest, click export.");
					alert.showAndWait();
				});
			}
			state.set("FINISHED");
		});
		
		Thread lockTabThread = new Thread(()-> {
			Platform.runLater(()->{mainTAB.getTabs().add(loadingTab);});
			while (state.get().equals("LOADING")) 
				mainTAB.getSelectionModel().select(loadingTab);
			switch (state.get()) {
			case "FINISHED":
				mainTAB.getSelectionModel().select(1);
				break;
			case "CANCELLED":
				mainTAB.getSelectionModel().select(0);
				break;
			}
			Platform.runLater(()->{mainTAB.getTabs().remove(loadingTab);});
		});
		
		state.set("LOADING");
		searchThread.setDaemon(true);
		lockTabThread.setDaemon(true);
		searchThread.start();
		lockTabThread.start();
	}
	
	public static void cancelRequest() {
		state.set("CANCELLED");
		searchThread.interrupt();
	}
	
	private Label genLabelFromString(String title) {
		Label label = new Label(title);
		label.setStyle(label.getStyle()+";-fx-font-size: 30");
		label.setOnMouseClicked(e->{
			try {
				Tab pageTab = new Tab();
				pageTab.setGraphic(new Label(title));
				WebView page = new WebView();
				page.getEngine().load(blockList.getWikiURL()+"/wiki/"+title);
				pageTab.setContent(page);
				viewerTAB.getTabs().add(pageTab);
				viewerTAB.getSelectionModel().select(pageTab);
				mainTAB.getSelectionModel().select(2);
			} catch (Exception err) {
				err.printStackTrace();
			}
		});
		return label;
	}

	@FXML
	public void handleExportSearch(ActionEvent event) {
		File file = getFileChooser(new ExtensionFilter("JSON files (*.json)", "*.json")).showSaveDialog(null);
        if (file == null) return;

        JsonObject search = new JsonObject();
        
        JsonArray startTitles = new JsonArray();
        for (String title : startPagesTXT.getText().split("\n"))
        	startTitles.add(title);
        
        JsonArray blocks = new JsonArray();
        for (Block b : blockList.getBlockList()) 
        	blocks.add(b.toJson());
        
        search.addProperty("lang", languageSelection.getValue());
        search.add("start", startTitles);
        search.add("blocks", blocks);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UTFFileWrite(file, gson.toJson(search));
        
        Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Search successfully exported");
		alert.showAndWait();
	}
	
	@FXML
	public void handleImportSearch(ActionEvent event) {
		File file = getFileChooser(new ExtensionFilter("JSON files (*.json)", "*.json")).showOpenDialog(null);
        if (file == null) return;
        
        Gson gson = new Gson();
        JsonObject search = null;
        try {
        	search = gson.fromJson(new FileReader(file), JsonObject.class);
		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("File not found.");
			alert.showAndWait();
			return;
		} catch (JsonSyntaxException | JsonIOException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("JSON contents malformed.");
			alert.setContentText(e.getMessage().split(":")[1]);
			alert.showAndWait();
			return;
		}
        
        try {
        String lang = search.get("lang").getAsString();
        
        ArrayList<String> start = new ArrayList<String>();
        search.get("start").getAsJsonArray().forEach((e)->start.add(e.getAsString()));
        
        ArrayList<Block> blocks = new ArrayList<Block>();
        search.get("blocks").getAsJsonArray().forEach((blockJson)->{
        	JsonArray attributes = blockJson.getAsJsonObject().get("attributes").getAsJsonArray();
    		switch (blockJson.getAsJsonObject().get("blockType").getAsString()) {
    		case "TitleRegexBlock": blocks.add(TitleRegexBlock.jsonToBlock(attributes)); break;
    		case "TextRegexBlock": blocks.add(TextRegexBlock.jsonToBlock(attributes)); break;
    		case "LinksBlock": blocks.add(LinksBlock.jsonToBlock(attributes)); break;
    		case "IsLinkedByBlock": blocks.add(IsLinkedByBlock.jsonToBlock(attributes)); break;
    		case "IsInCategoryBlock": blocks.add(IsInCategoryBlock.jsonToBlock(attributes)); break;
    		case "FromCategoryBlock": blocks.add(FromCategoryBlock.jsonToBlock(attributes)); break;
    		}
        });
        
        blockList.setBlockList(blocks);
        
        languageSelection.setValue(search.get("lang").getAsString());
		languageSelection.getOnAction().handle(null);
		
		search.get("start").getAsJsonArray().forEach((e->{
			startPagesTXT.setText(startPagesTXT.getText() + e.getAsString() + "\n");
		}));
		startPagesTXT.setText(startPagesTXT.getText().strip());
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Search successfully imported.");
		alert.showAndWait();
        } catch (Exception e) {
        	Alert alert = new Alert(AlertType.ERROR);
    		alert.setHeaderText("JSON does not contain valid search data.");
    		alert.showAndWait();
        }
	}
	
	@FXML
	public void handleExport(ActionEvent event) {
		File file = getFileChooser(new ExtensionFilter("TXT files (*.txt)", "*.txt")).showSaveDialog(null);
        if (file == null) return;
        
        String writeString = "";
        for (String title : results) 
			writeString += title+"\r\n";
        
		UTFFileWrite(file, writeString);
	}

	private FileChooser getFileChooser(ExtensionFilter extFilter) {
		FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser;
	}
	
	private void UTFFileWrite(File outFile, String write) {
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), "UTF8"));
			out.write(write);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void errorAlert(String head, String content) {
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(head);
			alert.setContentText(content);
			alert.showAndWait();
		});
		cancelRequest();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//reset image
		cornerIMG.setImage(new Image(getClass().getResourceAsStream("/wikisearch.png")));
		
		//bug in javafx requires this line for tabs to work properly
		mainTAB.setStyle("-fx-code-tab-animation: none");
		
		//Custom i18nUpdater class, used to update strings between languages.
		i18nUpdater = new InternationalStringUpdater(Main.EN_RB);
		i18nUpdater.add("AddBlocks", addBlocksLBL.textProperty());
		i18nUpdater.add("AddedBlocks", addedBlocksLBL.textProperty());
		i18nUpdater.add("EditBlocks", editBlocksLBL.textProperty());
		i18nUpdater.add("StartPages", startPagesLBL.textProperty());
		i18nUpdater.add("EditTab", editSearchTabLBL.textProperty());
		i18nUpdater.add("ResultTab", searchResultsTabLBL.textProperty());
		i18nUpdater.add("ViewerTab", viewerTitleLBL.textProperty());
		i18nUpdater.add("Title", titleLBL.textProperty());
		i18nUpdater.add("StartButton", startSearchBTN.textProperty());
		i18nUpdater.add("Instruction", instructionLBL.textProperty());
		i18nUpdater.add("CategoryLang", IsInCategoryBlock.getCategoryLang());
		i18nUpdater.add("ExportButton", exportResultsBTN.textProperty());
		i18nUpdater.add("ExportSearch", exportSearchBTN.textProperty());
		i18nUpdater.add("ImportSearch", importSearchBTN.textProperty());
		i18nUpdater.add("StartPrompt", startPagesTXT.promptTextProperty());
		
		//Combobox to change languages. 
		languageSelection = new ComboBox<String>(FXCollections.observableArrayList(new String[] {
				"English", "French"
		}));
		languageSelection.setOnAction((ActionEvent e)->{
			String lang = languageSelection.getValue();
			switch (lang) {
			case "English": i18nUpdater.update(Main.EN_RB); break;
			case "French": i18nUpdater.update(Main.FR_RB); break;
			}
		});
		buttonHBOX.getChildren().add(languageSelection);
		
		//Loading tab, generated in code so that loading animation does not appear
		//immediately at the start of program, only when user is waiting for results.
		loadingScreen = new LoadingScreen();
		loadingTab = new Tab();
		loadingTab.setContent(loadingScreen.getPane());
		Label loadingTabLBL = new Label("Loading...");
		loadingTab.setGraphic(loadingTabLBL);
		i18nUpdater.add("Loading", loadingTabLBL.textProperty());
		i18nUpdater.add("TimeElapsed", loadingScreen.getTimeElapsedStr());
		
		//setup for custom class BlockList
		blockList = new BlockList();
		blockList.bindToPane(blocksPANE);
		blockList.bindEditPane(editBlockPANE);
		//i18nUpdater used to change the wikipedia url from en.wikipedia to fr.wikipedia
		i18nUpdater.add("WikiLanguage", blockList.getWikiLang());
		
		//setup for block adding 
		for (Class<? extends Block> blockClass : blocks) {
			try {
				blockClass.getConstructor().newInstance();
			} catch (Exception err){
				err.printStackTrace();
				//if error, do not allow user to add this block.
				continue;
			}
			
			//Setup for "add blocks" buttons
			//Buttons generated dynamically due to large number of buttons which have similar functionality
			Button button = new Button(blockClass.getSimpleName());
			button.setMinWidth(150);
			button.setPrefHeight(999999);	
			button.setStyle("-fx-font-size: 20px; -fx-wrap-text: true;");
			HBox.setHgrow(button, Priority.ALWAYS);
			button.setMaxWidth(Double.MAX_VALUE);
			button.setOnAction((ActionEvent e)->{
				try {
					blockList.add(blockClass.getConstructor().newInstance());
				} catch (Exception err){}
				//ignore catch as was already checked at start
			});
			//i18nUpdater used to change block text 
			i18nUpdater.add(blockClass.getSimpleName(), button.textProperty());
			blockAddPANE.getChildren().add(button);	
		}
		
		//Set english as default language
		languageSelection.setValue("English");
		languageSelection.getOnAction().handle(null);
	}

	public static InternationalStringUpdater getI18nUpdater() {
		return i18nUpdater;
	}
	
	public static LoadingScreen getLoadingScreen() {
		return loadingScreen;
	}
}
