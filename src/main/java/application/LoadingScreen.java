package application;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalDateTime;

/*
 * This class is used to wrap LoadingPane.fxml and LoadingPaneController
 * It makes code neater by abstracting away the initialization of the pane and controller. 
 * It contains references to a pane and controller object,
 * and some helper functions that cannot be put controller class
 * MVC classification: C
 */

public class LoadingScreen {
	private Pane pane;
	private LoadingPaneController controller;
	
	public LoadingScreen() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoadingPane.fxml"));
			pane = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setStatusText(String status) {
		Platform.runLater(()->{controller.getStatusLBL().setText(status);});
	}
	
	public void setStatusBar(double status) {
		controller.getStatusBAR().setProgress(status);
	}
	
	public Pane getPane() {
		return pane;
	}
	
	public StringProperty getTimeElapsedStr() {
		return controller.getTimeElapsedStr();
	}

	public void setStatus(int length, String blockName, int numBlocks, int totalBlocks) {
		setStatusText(""+length+" pages recieved by "+blockName+"\n"+
				numBlocks+" of "+totalBlocks+" blocks completed.");
		setStatusBar((double)numBlocks/(double)totalBlocks);
	}

	public void restart() {
		controller.setStartTime(LocalDateTime.now());
	}
}
