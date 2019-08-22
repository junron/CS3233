package application;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.User;
import models.cars.Car;
import models.cars.Economy;
import storage.CarStorage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class GalleryController implements Initializable {
  private static GalleryController galleryController;
  private static User user;
  public Text welcome;
  public GridPane gridpane;
  public Slider maxPrice;
  public ComboBox<Object> brand;
  public ComboBox type;
  private ArrayList<Car> cars;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GalleryController.galleryController = this;
    cars = CarStorage.storage.filter(car -> car instanceof Economy);
    int i = 0;
    for (Car car : cars) {
      int row = Math.floorDiv(i, 4);
      int column = i % 4;
      gridpane.add(createPane(car), column, row);
      i++;
    }
    brand.setItems(observableArrayList(CarStorage.storage.mapUnique(car -> car.getBrandAndModel().split(" - ")[0])));
    if (user != null) {
      galleryController.welcome.setText("Welcome, " + user.getName());
    }
  }

  private VBox createPane(models.cars.Car car) {
    VBox vBox = new VBox();
    ObservableList<Node> children = vBox.getChildren();

    Text title = new Text(car.getBrandAndModel());
    title.getStyleClass().add("title");
    children.add(title);

    children.add(car.getImage());

    GridPane gridPane = new GridPane();
    gridPane.add(new Text(car.getType()), 0, 0);
    gridPane.add(new Text("Price:"), 0, 1);
    gridPane.add(new Text("$" + car.getHourlyCharge()), 1, 1);
    Button rent = new Button("Order");
    gridPane.add(rent, 0, 2, 2, 1);
    GridPane.setHalignment(rent, HPos.CENTER);
    gridPane.getStyleClass().addAll("carPanePadding", "carGridPane");
    children.add(gridPane);

    vBox.getStyleClass().addAll("carPanePadding", "carVbox");
    return vBox;
  }

  public static void setUser(User user) {
    GalleryController.user = user;
    if (galleryController == null) return;
    galleryController.welcome.setText("Welcome, " + user.getName());
  }

  public void brandChange(ActionEvent actionEvent) {
  }
}
