package application;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.*;

//MVC classification: C

public class Main extends Application {
	public static final ResourceBundle EN_RB = ResourceBundle.getBundle("UIText", new Locale("en"));
	public static final ResourceBundle FR_RB = ResourceBundle.getBundle("UIText", new Locale("fr"));
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/wikisearch.png")));
		primaryStage.setTitle("WikiSearch is loading...");
		
		/*
		 * futureAppNode is the Parent object that will eventually be returned
		 * by the background thread running in getAppNode(). This way, the long 
		 * initialization where nothing can be displayed is avoided.
		 * While getAppNode() is running in the background,
		 * openSplash(primaryStage) loads the splashScreen which loads much faster,
		 * displaying the splash screen while the main screen takes a while more to load.
		 * once getAppNode() finishes, splashScreen sends a signal to begin the 
		 * transition that fades out the splash screen and fades in the main screen.
		 */
		Future<Parent> futureAppNode = getAppNode();
		
		Node splashNode = openSplash(primaryStage);
		FadeTransition splashOut = new FadeTransition();
		splashOut.setNode(splashNode);
		splashOut.setFromValue(1);
		splashOut.setToValue(0);
		splashOut.setDuration(Duration.seconds(2));
		splashOut.setOnFinished((e)->{
			Parent appNode = null;
			try {
				appNode = futureAppNode.get();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
				Platform.exit();
			}
			
			FadeTransition appIn = new FadeTransition();
			appIn.setNode(appNode);
			appIn.setFromValue(0);
			appIn.setToValue(1);
			appIn.setDuration(Duration.seconds(1));
			
			Scene scene = new Scene(appNode);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			appIn.play();

			primaryStage.setTitle("WikiSearch");
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
		});
		
		splashScreenController.getIsDone().addListener((obs, oldVal, newVal)->{
			if (newVal) splashOut.play();
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Future<Parent> getAppNode() {
		//load main in seperate thread
		Callable<Parent> get = ()->{
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/Main.fxml"));
			return root;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}};
		ExecutorService service = Executors.newSingleThreadExecutor();
		return service.submit(get);
	}
	
	public Node openSplash(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/splashScreen.fxml"));
			Scene scene = new Scene(root,880,650);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			return root;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
