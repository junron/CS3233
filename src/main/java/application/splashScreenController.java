package application;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

//MVC classification: C

public class splashScreenController implements Initializable{
	@FXML
	private ImageView magIMG;
	@FXML
	private ImageView logoIMG;
	@FXML
	private ProgressBar progressBAR;
	@FXML
	private Label statusLBL;
	
	private static SimpleBooleanProperty isDone = new SimpleBooleanProperty(false);
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		logoIMG.setImage(new Image(getClass().getResourceAsStream("/wikipedialogo.png")));
		magIMG.setImage(new Image(getClass().getResourceAsStream("/magnifyingglass.png")));
		//fake loading messages
		String[] loadingStrings = new String[] {
				"code",
				"code - Success",
				"css",
				"css - Success",
				"API",
				"API - Success",
		};
		
		Duration timeLoad = Duration.seconds(10);
		Thread animationThread = new Thread(()->{
			double radius = 120;
			double state = 0;
			double angle = 0;
			double magScaleRate = 1.1;
			
			while (true) {
				
				 //Increment the progress bar so that after timeLoad seconds, bar is full.
				 //At regular intervals, change the loading messages.
				 
				state += 20/timeLoad.toMillis();
				int index = (int)(state*loadingStrings.length);
				Platform.runLater(()->{
					if (index < loadingStrings.length) 
						statusLBL.setText("Loading "+loadingStrings[index]);
					else
						statusLBL.setText("Done!");
				});
				angle += 0.05;
				magIMG.setTranslateX(Math.sin(angle)*radius);
				magIMG.setTranslateY(Math.cos(angle)*radius);
				progressBAR.setProgress(state);
				logoIMG.setRotate(angle*20);
				
				//Once bar is loaded, begin transition animation.
				//Loading text is set to "Done!"
				//Magnifying glass starts to grow 
				if (state > 1) {
					isDone.set(true);
					radius = Math.max(0, radius-5);
					magIMG.setScaleX(magIMG.getScaleX() * magScaleRate);
					magIMG.setScaleY(magIMG.getScaleY() * magScaleRate);
				}
				
				//Once state > 2 (aka past 100% loaded), Main.fxml has likely been loaded.
				//animation thread can be killed.
				if (state > 2) {
					break;
				}
				
				//20ms refresh rate
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		animationThread.setDaemon(true);
		animationThread.start();
	}

	public static SimpleBooleanProperty getIsDone() {
		return isDone;
	}
}
