package application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import models.Transaction;
import models.User;
import models.cars.Car;
import models.cars.Limousine;
import storage.CarStorage;
import storage.TransactionStorage;
import utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class GalleryController implements Initializable {
  public static GalleryController galleryController;
  private User user;
  private LocalDateTime startTime, returnTime;
  @FXML
  private Text welcome;
  @FXML
  private GridPane gridpane;
  @FXML
  private Slider maxPrice;
  @FXML
  private ComboBox<Object> brand;
  @FXML
  private ComboBox type;
  @FXML
  private Text maxPriceOut;
  @FXML
  private TextField searchField;
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
    if (cars.size() == 0) {
      Text t = new Text("No cars available");
      t.setFont(Font.font("Comic Sans MS", 24));
      GridPane.setHalignment(t, HPos.CENTER);
      gridpane.add(t, 0, 0, 4, 1);
      return;
    }
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
    if (maxPriceOut.getText().equals("Text")) {
      maxPrice.setValue(maxPrice.getMax());
      maxPriceOut.setText("$" + maxPrice.getMax());
    }
  }

  private Pane createPane(Car car) {
    VBox vBox = new VBox();
    ObservableList<Node> children = vBox.getChildren();

    HBox title = Utils.showSearch(car.getBrandAndModel(), searchField.getText());
    title.getStyleClass().add("title");
    children.add(title);

    children.add(car.getImage());

    GridPane gridPane = new GridPane();
    gridPane.add(Utils.showSearch(car.getType(), searchField.getText()), 0, 0, 2, 1);
    gridPane.add(new Text("Price:"), 0, 1);
    gridPane.add(new Text(String.format("$%.2f", car.getHourlyCharge())), 1, 1);
    if (car instanceof Limousine) {
      gridPane.add(new Text("Has chauffeur:"), 0, 2);
      gridPane.add(new Text(((Limousine) car).isHasChauffeur() ? "Yes" : "No"), 1, 2);
    }
    Button rent = new Button("Order");
    rent.setOnMouseClicked(e -> handleOrder(car));
    if (car instanceof Limousine) {
      gridPane.add(rent, 0, 3, 2, 1);
    } else {
      gridPane.add(rent, 0, 2, 2, 1);
    }
    GridPane.setHalignment(rent, HPos.CENTER);
    gridPane.getStyleClass().addAll("carPanePadding", "carGridPane");
    children.add(gridPane);

    vBox.getStyleClass().addAll("carPanePadding", "carVbox");
    return vBox;
  }

  private void handleOrder(Car car) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Confirm order and pay");
    try {
      dialog.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("/carinfo.fxml")));
    } catch (IOException e) {
      return;
    }
    dialog.getDialogPane().getButtonTypes().addAll(CarInfoController.confirmButton, ButtonType.CANCEL);
    Transaction transaction = new Transaction(startTime, returnTime, car, user);
    CarInfoController.carInfoController.setTransaction(transaction, false);
    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent() && result.get().equals(CarInfoController.confirmButton)) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Payment successful");
      alert.setHeaderText("Payment received");
      alert.setContentText(String
              .format("Your payment of $%.2f has been received. \nThank you for using Amazing Car Sharing", transaction
                      .computeCost()));
      alert.showAndWait();

      Alert saveBillAlert = new Alert(Alert.AlertType.CONFIRMATION);
      saveBillAlert.setTitle("Save bill");
      saveBillAlert.setHeaderText("Please save your bill to a file so that you can refer to it later.");
      CarInfoController.carInfoController.setTransaction(transaction, true);
      saveBillAlert.getButtonTypes().removeIf(buttonType -> buttonType.equals(ButtonType.OK));
      ButtonType saveBill = new ButtonType("Save bill", ButtonBar.ButtonData.YES);
      saveBillAlert.getButtonTypes().add(0, saveBill);
      Optional<ButtonType> res = saveBillAlert.showAndWait();
      if (res.isPresent() && res.get().equals(saveBill)) {
        CarInfoController.carInfoController.triggerBillSave();
      }
      TransactionStorage.storage.addTransaction(transaction);
      ScreenController.activate("dashboard");
    }
  }

  void setUser(User user) {
    this.user = user;
    if (galleryController == null) return;
    galleryController.welcome.setText("Welcome, " + user.getName());
  }

  private boolean filter(Car car) {
    for (Transaction transaction : TransactionStorage.storage.getTransactionByCarPlate(car.getRegistrationNum())) {
      //      Check for intersection
      //     https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
      if (startTime.compareTo(transaction.getReturnTime()) <= 0 && returnTime
              .compareTo(transaction.getStartTime()) >= 0) return false;
    }
    if (searchField.getText() != null && searchField.getText().trim().length() > 0) {
      return car.search().contains(searchField.getText().toLowerCase());
    }
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

  @FXML
  private void filterChange() {
    cars = CarStorage.storage.filter(this::filter);
    render();
  }

  @FXML
  private void triggerBack() {
    ScreenController.activate("dashboard");
  }

  void setReturnTime(LocalDateTime returnTime) {
    this.returnTime = returnTime;
    if (this.startTime != null) {
      cars = CarStorage.storage.filter(a -> true);
      filterChange();
    }
  }

  void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
    if (this.returnTime != null) {
      cars = CarStorage.storage.filter(a -> true);
      filterChange();
    }
  }

}
