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
import storage.CarStorage;
import utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class GalleryController implements Initializable {
  private static GalleryController galleryController;
  private static User user;
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
  @FXML
  private Button checkoutBtn;
  private ArrayList<Car> cars;
  private ArrayList<Transaction> pendingTransactions = new ArrayList<>();

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
    gridPane.add(new Text("$" + car.getHourlyCharge()), 1, 1);
    Button rent = new Button("Order");
    rent.setOnMouseClicked(e -> handleOrder(car, rent.getText().equals("Order")));
    gridPane.add(rent, 0, 2, 2, 1);
    GridPane.setHalignment(rent, HPos.CENTER);
    gridPane.getStyleClass().addAll("carPanePadding", "carGridPane");
    children.add(gridPane);

    vBox.getStyleClass().addAll("carPanePadding", "carVbox");
    for (Transaction transaction : pendingTransactions) {
      if (transaction.getCar().getRegistrationNum().equals(car.getRegistrationNum())) {
        rent.setWrapText(true);
        rent.setText("Cancel order");
        vBox.setStyle("-fx-background-color: gray");
        vBox.setOpacity(0.7);
      }
    }
    return vBox;
  }

  private void handleOrder(Car car, boolean ordering) {
    if (!ordering) {
      Transaction transaction = null;
      for (Transaction t : pendingTransactions) {
        if (t.getCar().getRegistrationNum().equals(car.getRegistrationNum())) {
          transaction = t;
          break;
        }
      }
      if (transaction == null) return;
      pendingTransactions.remove(transaction);
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Order cancellation");
      alert.setHeaderText("Order cancelled");
      alert.setContentText("Your order for car " + transaction.getCar().getRegistrationNum() + " has been cancelled.");
      alert.showAndWait();
      render();
      return;
    }
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Confirm order");
    try {
      dialog.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("/carinfo.fxml")));
    } catch (IOException e) {
      return;
    }
    dialog.getDialogPane().getButtonTypes().addAll(CarInfoController.confirmButton, ButtonType.CANCEL);
    CarInfoController.setCar(car);
    CarInfoController.setUser(user);
    CarInfoController.setDialog(dialog);
    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent()) {
      if (result.get().equals(CarInfoController.confirmButton)) {
        pendingTransactions.add(CarInfoController.getTransaction());
        checkoutBtn.setDisable(false);
        render();
      }
    }
  }

  public static void setUser(User user) {
    GalleryController.user = user;
    if (galleryController == null) return;
    galleryController.welcome.setText("Welcome, " + user.getName());
  }

  private boolean filter(Car car) {
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
  private void triggerCheckout() {
    CheckoutController.checkoutController.setTransactions(pendingTransactions);
    ScreenController.activate("checkout");
  }
}
