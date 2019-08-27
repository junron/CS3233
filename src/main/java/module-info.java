module main {
  exports models;
  opens application;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.swing;
  exports application;
  exports models.cars;
}
