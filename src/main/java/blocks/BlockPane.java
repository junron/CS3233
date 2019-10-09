package blocks;

import application.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

//MVC classification: C

/*
 * BlockPane, like LoadingScreen, is a wrapper for BlockPane.fxml and BlockPaneController.
 * It makes code neater by abstracting away the initialization of the pane and controller.
 */

public class BlockPane {
	private Pane pane;
	private BlockPaneController controller;
	
	BlockPane(BlockList containingList, Block block, String idk){
		this(containingList, block);
	}
	
	BlockPane(BlockList containingList, Block block){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/BlockPane.fxml"));
			pane = loader.load();
			controller = loader.getController();
			controller.setContainingList(containingList);
			controller.setBlock(block);
			MainController.getI18nUpdater().add(block.getClass().getSimpleName(), controller.getStringProperty());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Pane getPane(String string) {
		return pane;
	}
	public BlockPaneController getController() {
		return controller;
	}
	
	
}
