package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main extends Application {

  private boolean login = false;
  private WebEngine webEngine;
  private TitledPane loginTitlePane;
  private TextField userNameField = new TextField();
  private PasswordField passwordField = new PasswordField();
  private Button loginBtn = new Button("Login");
  private Button logoutBtn = new Button("Logout");
  private final TableView<Population> table = new TableView<Population>();
  private final ObservableList<Population> data = FXCollections.observableArrayList();
  private VBox rightBox = new VBox();
  private Label bottomPanelLabel = new Label();
  private VBox bottomBox = new VBox();


  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Lab 5");
    primaryStage.setScene(new Scene(createContent()));
    primaryStage.show();
  }

  private Parent createContent() {
    BorderPane root = new BorderPane();
    root.setLeft(createLeftPanel());
    root.setCenter(createCentrePanel());
    root.setBottom(createBottomPanel());
    root.setRight(createRightPanel());
    return root;
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Task 2
   *
   * @return
   */
  private VBox createLeftPanel() {
    VBox vbox = new VBox();
    /**
     * Event handling for accordion
     * Helping codes
     *
     */
    Label userInfoLabel = new Label();
    TitledPane userInfoTitledPane = new TitledPane("", userInfoLabel);

    final Accordion accordion = new Accordion();
    GridPane gp = new GridPane();
    gp.add(new Label("User: "), 0, 0);
    gp.add(new Label("Password: "), 0, 1);
    gp.add(loginBtn, 0, 2);
    logoutBtn.setDisable(true);
    gp.add(logoutBtn, 1, 2);
    gp.add(userNameField, 1, 0);
    gp.add(passwordField, 1, 1);
    userInfoTitledPane.setDisable(true);
    loginBtn.setOnMouseClicked(event -> {
      if (userNameField.getText().equals("hello")) {
        if (passwordField.getText().equals("WORLD")) {
          this.login = true;
          this.loginBtn.setDisable(true);
          this.logoutBtn.setDisable(false);
          this.loginTitlePane.setText("You are logged in");
          showBarChart();
          showBottomPanel();
          userInfoTitledPane.setDisable(false);
        }
      }
    });
    logoutBtn.setOnMouseClicked(event -> {
      this.login = false;
      this.loginBtn.setDisable(false);
      this.logoutBtn.setDisable(true);
      this.loginTitlePane.setText("Login");
      hideBottomPanel();
      userInfoTitledPane.setDisable(true);
    });
    loginTitlePane = new TitledPane("Login", gp);
    accordion.getPanes().addAll(loginTitlePane, userInfoTitledPane);

    accordion.expandedPaneProperty().addListener((arg0, arg1, arg2) -> {
      if (arg2 != null && login) {
        Date now = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("yyy/MM/dd hh:mm:ss");
        userInfoLabel.setText(dtf.format(now) + "\nWelcome! ");
      }
    });
    vbox.getChildren().add(accordion);


    return vbox;

  }

  private VBox createCentrePanel() {
    VBox vbox = new VBox();
    Label label = new Label("Enter url");
    TextField tf = new TextField();
    WebView browser = new WebView();
    webEngine = browser.getEngine();
    webEngine.load("https://nushigh.edu.sg/");
    tf.setOnKeyPressed(event -> {
      try {
        URL url = new URL(tf.getText());
        webEngine.load(String.valueOf(url));
      } catch (MalformedURLException ignored) {
      }
    });
    vbox.getChildren().addAll(label, tf, browser);
    tf.setText("https://nushigh.edu.sg/");
    return vbox;
  }

  private VBox createBottomPanel() {

    bottomPanelLabel.textProperty().setValue("Sex composition of resident population");
    bottomPanelLabel.setFont(new Font("Arial", 20));
    createTable();
    return bottomBox;
  }

  public void showBottomPanel() {
    bottomBox.getChildren().add(bottomPanelLabel);
    bottomBox.getChildren().add(table);

  }

  public void hideBottomPanel() {
    bottomBox.getChildren().remove(bottomPanelLabel);
    bottomBox.getChildren().remove(table);

  }

  public VBox createRightPanel() {
    return this.rightBox;
  }

  public void clearRightPanel() {
    this.rightBox.getChildren().clear();
  }

  private void createTable() {
    table.setEditable(true);

    TableColumn yearCol = new TableColumn("Year");
    yearCol.setMinWidth(100);
    yearCol.setCellValueFactory(
            new PropertyValueFactory<>("year"));

    TableColumn totalCol = new TableColumn("Total");
    totalCol.setMinWidth(100);
    totalCol.setCellValueFactory(
            new PropertyValueFactory<>("total"));

    TableColumn numOfMaleCol = new TableColumn("Number of Male resident");
    numOfMaleCol.setMinWidth(100);
    numOfMaleCol.setCellValueFactory(
            new PropertyValueFactory<>("numOfMale"));

    TableColumn numOfFemaleCol = new TableColumn("Number of Female resident");
    numOfFemaleCol.setCellValueFactory(
            new PropertyValueFactory<>("numOfFemale"));
    numOfFemaleCol.setMinWidth(100);


    try {

      BufferedReader br = new BufferedReader(
              new InputStreamReader(getClass().getResourceAsStream("/SexCompositionResidentPopulation.csv")));


      String line = null;

      line = br.readLine();
      //To read from file
      while ((line = br.readLine()) != null) {
        System.out.println(line);
        Scanner sc = new Scanner(line);
        sc.useDelimiter(",");
        data.add(new Population(sc.next(), Integer.parseInt(sc.next()),
                Integer.parseInt(sc.next()),
                Integer.parseInt(sc.next())));

        sc.close();

      }//end while

      br.close();

    } catch (Exception ex) {
      System.out.println("Error" + ex.getMessage());
    }

    table.setItems(data);
    table.getColumns().addAll(yearCol, totalCol, numOfMaleCol, numOfFemaleCol);
  }

  private void showBarChart() {
    if (login) {
      final CategoryAxis xAxis = new CategoryAxis();
      final NumberAxis yAxis = new NumberAxis();
      final BarChart<String, Number> bc =
              new BarChart<>(xAxis, yAxis);

      xAxis.setLabel("Year");
      yAxis.setLabel("Value");

      XYChart.Series[] series = new XYChart.Series[3];
      for (int j = 0; j < series.length; j++) {
        series[j] = new XYChart.Series();

      }

      for (Population p : data) {
        series[0].getData().add(new XYChart.Data(p.getYear(), p.getTotal()));
        series[0].setName("Total resident population");
        series[1].getData().add(new XYChart.Data(p.getYear(), p.getNumOfMale()));
        series[1].setName("Male");
        series[2].getData().add(new XYChart.Data(p.getYear(), p.getNumOfFemale()));
        series[2].setName("Female");
      }

      for (XYChart.Series value : series) bc.getData().add(value);
      rightBox.getChildren().add(bc);
    }
  }
}
