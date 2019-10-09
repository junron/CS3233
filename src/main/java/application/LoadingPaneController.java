package application;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

//MVC classification: C

public class LoadingPaneController implements Initializable{
	@FXML
	private Label statusLBL;
	@FXML
	private ProgressBar statusBAR;
	@FXML
	private ImageView wikiIMG;
	@FXML
	private Label timerLBL;
	
	private LocalDateTime startTime = LocalDateTime.now();
	private SimpleStringProperty timeElapsedStr = new SimpleStringProperty("Time elapsed");

	// Event Listener on Button.onAction
	@FXML
	public void handleCancel(ActionEvent event) {
		MainController.cancelRequest();
	}

	public Label getStatusLBL() {
		return statusLBL;
	}

	public void setStatusLBL(Label statusLBL) {
		this.statusLBL = statusLBL;
	}

	public ProgressBar getStatusBAR() {
		return statusBAR;
	}

	public void setStatusBAR(ProgressBar statusBAR) {
		this.statusBAR = statusBAR;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		wikiIMG.setImage(new Image(getClass().getResourceAsStream("/wikipedialogo.png")));
		
		//rotation animation
		(new Thread(()-> {
			while (true) {
				Platform.runLater(()->{
					wikiIMG.setRotate(wikiIMG.getRotate()+1);
					timerLBL.setText(timeElapsedStr.getValue()+":\n"+timeElapsedString());
				});
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		})).start();;
	}

	public void setStartTime(LocalDateTime time) {
		startTime = time;		
	}
	
	public StringProperty getTimeElapsedStr() {
		return timeElapsedStr;
	}
	
	private String timeElapsedString() {
		LocalDateTime now = LocalDateTime.now();
		long minutes = ChronoUnit.MINUTES.between(startTime, now);
		long seconds = ChronoUnit.SECONDS.between(startTime, now) % 60;
		return (minutes !=0 ? minutes+"min " : "") + seconds+"s";
	}
}
