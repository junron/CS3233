package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import models.Transaction;
import storage.TransactionStorage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminChartController implements Initializable {
  public static AdminChartController adminChartController;
  private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  @FXML
  private CategoryAxis xAxis;
  @FXML
  private PieChart typesOfCars;
  @FXML
  private LineChart<String, Number> perMonth;

  @FXML
  private void triggerBack() {
    ScreenController.activate("admin");
  }

  void render() {
    typesOfCars.getData().clear();
    typesOfCars.setLegendVisible(true);
    typesOfCars.setLegendSide(Side.BOTTOM);
    ArrayList<Transaction> transactions = TransactionStorage.storage.filter(_t -> true);
    Map<String, Integer> cars = new HashMap<>();
    for (Transaction transaction : transactions) {
      if (!cars.containsKey(transaction.getCar().getType())) {
        cars.put(transaction.getCar().getType(), 1);
      } else {
        System.out.println(transaction.getCar().getType()+cars.containsKey(transaction.getCar().getType()));
        cars.put(transaction.getCar().getType(), cars.get(transaction.getCar().getType()) + 1);
      }
    }
    for (Map.Entry entry : cars.entrySet()) {
      typesOfCars.getData().add(new PieChart.Data((String) entry.getKey(), (Integer) entry.getValue()));
    }
  }

  @FXML
  public void rentals() {
    perMonth.getData().clear();
    ArrayList<Transaction> transactions = TransactionStorage.storage.filter(_t -> true);
    xAxis.setLabel("Month");
    Series<String, Number> series = new Series<>();
    int[] cars = new int[12];
    for (Transaction transaction : transactions) {
      LocalDateTime start = transaction.getStartTime();
      cars[start.getMonth().getValue() - 1]++;
    }
    for (int i = 0; i < 12; i++) {
      series.getData().add(new XYChart.Data<>(months[i], cars[i]));
    }
    perMonth.getData().add(series);
    perMonth.setLegendVisible(false);
  }

  @FXML
  private void revenue() {
    perMonth.getData().clear();
    ArrayList<Transaction> transactions = TransactionStorage.storage.filter(_t -> true);
    xAxis.setLabel("Month");
    Series<String, Number> series = new Series<>();
    double[] cars = new double[12];
    for (Transaction transaction : transactions) {
      LocalDateTime start = transaction.getStartTime();
      cars[start.getMonth().getValue() - 1] += transaction.computeCost();
    }
    for (int i = 0; i < 12; i++) {
      series.getData().add(new XYChart.Data<>(months[i], cars[i]));
    }
    perMonth.getData().add(series);
    perMonth.setLegendVisible(false);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    adminChartController = this;
  }
}
