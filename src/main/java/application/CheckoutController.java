package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.Transaction;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CheckoutController implements Initializable {
  static CheckoutController checkoutController;
  @FXML
  private Accordion accordion;
  @FXML
  private Label totalCost;
  private ArrayList<Transaction> transactions;
  private String totalCostString;


  private void render() {
    double cost = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    for (Transaction transaction : transactions) {
      TitledPane titledPane = new TitledPane();
      titledPane.setText("Bill #" + transaction.getSerialNumber());
      GridPane pane = new GridPane();
      pane.setAlignment(Pos.CENTER);
      pane.setHgap(20);
      pane.setVgap(10);
      pane.add(new Text("Serial number:"), 0, 0);
      pane.add(new Text("" + transaction.getSerialNumber()), 1, 0);
      pane.add(new Text("Name:"), 0, 1);
      pane.add(new Text(transaction.getUser().getName()), 1, 1);
      pane.add(new Text("Car registration number:"), 0, 2);
      pane.add(new Text(transaction.getCar().getRegistrationNum()), 1, 2);
      pane.add(new Text("Start time:"), 0, 3);
      pane.add(new Text(dateFormat.format(transaction.getStartTime())), 1, 3);
      pane.add(new Text("Return time:"), 0, 4);
      pane.add(new Text(dateFormat.format(transaction.getReturnTime())), 1, 4);
      pane.add(new Text("Total hours:"), 0, 5);
      pane.add(new Text("" + transaction.getHours()), 1, 5);
      pane.add(new Text("Cost per hour:"), 0, 6);
      pane.add(new Text(String.format("$%.2f", transaction.getCar().getHourlyCharge())), 1, 6);
      pane.add(new Text("Subtotal:"), 0, 7);
      pane.add(new Text(String.format("$%.2f", transaction.computeCost())), 1, 7);
      titledPane.setContent(pane);
      cost += transaction.computeCost();
      accordion.getPanes().add(titledPane);
    }
    totalCost.setText(String.format("Total: $%.2f", cost));
    this.totalCostString = String.format("$%.2f", cost);
  }

  public void setTransactions(ArrayList<Transaction> transactions) {
    this.transactions = transactions;
    render();
  }

  @FXML
  public void triggerPayment() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Payment successful");
    alert.setHeaderText("Payment received");
    alert.setContentText("You payment of " + this.totalCostString + " has been received. \nThank you for using Amazing " +
            "Car Sharing");
    alert.showAndWait();
  }

  @FXML
  public void triggerCancel() {
    this.transactions.removeIf(e -> true);
    ScreenController.activate("gallery");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    CheckoutController.checkoutController = this;
  }
}
