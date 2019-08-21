package application;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.User;
import models.cars.Car;
import models.cars.Economy;
import storage.CarStorage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GalleryController implements Initializable {
  private static GalleryController galleryController;
  private static User user;
  public Pagination pagination;
  public Text welcome;
  private ArrayList<Car> economyCars;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GalleryController.galleryController = this;
    economyCars = CarStorage.storage.filter(car -> car instanceof Economy);
    pagination.setPageCount(economyCars.size());
    pagination.setPageFactory(index -> createPane(economyCars.get(index)));
  }
  private VBox createPane(Car car){
    VBox vBox = new VBox();
    ObservableList<Node> children = vBox.getChildren();
    children.add(car.getImage());

    GridPane gridPane = new GridPane();
    Text title = new Text(car.getBrandAndModel());
    title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 14));
    gridPane.add(title,0,0,2,1);
    gridPane.add(new Text("Price:"),0,1);
    gridPane.add(new Text("$"+car.getHourlyCharge()),1,1);
    Button rent = new Button("Rent");
    gridPane.add(rent,0,2,2,1);
    GridPane.setHalignment(rent, HPos.CENTER);

    gridPane.setPadding(new Insets(30,0,30,0));
    gridPane.setVgap(5);
    gridPane.setHgap(5);
    gridPane.setAlignment(Pos.BOTTOM_CENTER);
    children.add(gridPane);

    vBox.setAlignment(Pos.BOTTOM_CENTER);
    return vBox;
  }

  public static void setUser(User user) {
    GalleryController.user = user;
    galleryController.welcome.setText("Welcome, " + user.getName());
  }
}
