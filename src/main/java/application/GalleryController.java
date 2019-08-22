package application;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import models.User;
import models.cars.Car;
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
  public Text maxPriceOut;
  private ArrayList<Car> cars;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GalleryController.galleryController = this;
    cars = CarStorage.storage.filter(a -> true);
    render();
    brand.setItems(observableArrayList(CarStorage.storage.mapUnique(car -> car.getBrandAndModel().split(" - ")[0])));
    brand.getItems().add(0, "All");
    if (user != null) {
      galleryController.welcome.setText("Welcome, " + user.getName());
    }
    maxPrice.valueProperty().addListener((a, b, val) -> {
      maxPriceOut.setText("$" + Math.round((Double) val));
      filterChange();
    });
  }

  private void render() {
    gridpane.getChildren().removeAll(gridpane.getChildren());
    int i = 0;
    for (Car car : cars) {
      int row = Math.floorDiv(i, 4);
      int column = i % 4;
      gridpane.add(createPane(car), column, row);
      i++;
    }
    ArrayList<Object> prices = CarStorage.storage.mapUnique(Car::getHourlyCharge);
    maxPrice.setMax(prices.stream().mapToDouble(a -> {
      if (a instanceof Double) return (double) a;
      return -1;
    }).max().getAsDouble());

    maxPrice.setMin(prices.stream().mapToDouble(a -> {
      if (a instanceof Double) return (double) a;
      return Double.MAX_VALUE;
    }).min().getAsDouble());
    if (maxPrice.getMin() - maxPrice.getMax() > 2000) {
      maxPrice.setMajorTickUnit(1000);
    } else {
      maxPrice.setMajorTickUnit(100);
    }
    if (maxPriceOut.getText().equals("Text")) {
      maxPrice.setValue(maxPrice.getMax());
      maxPriceOut.setText("$" + maxPrice.getMax());
    }
    maxPrice.setLabelFormatter(new StringConverter<Double>() {
      @Override
      public String toString(Double object) {
        int num = (int) (double) object;
        if (num >= 1000) {
          return "" + Math.floorDiv(num, 1000) + "." + Math.floorDiv(num % 1000, 100) + "k";
        }
        return object.toString();
      }

      @Override
      public Double fromString(String string) {
        return null;
      }
    });
    //    maxPrice.setMin();
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

  public boolean filter(Car car) {
    String brand = (String) this.brand.getSelectionModel().getSelectedItem();
    if (brand != null && !brand.equals("All")) {
      if (!car.getBrandAndModel().split(" - ")[0].equals(brand)) {
        return false;
      }
    }
    String type = (String) this.type.getSelectionModel().getSelectedItem();
    if (type != null && !type.equals("All")) {
      if (!car.getType().equals(type)) {
        return false;
      }
    }
    double charge = maxPrice.getValue();
    return car.getHourlyCharge() <= charge;
  }

  public void filterChange() {
    cars = CarStorage.storage.filter(this::filter);
    render();
  }
}
