module main {
  exports models;
  opens application;
  requires javafx.controls;
  requires javafx.fxml;
  exports application;
  exports models.cars;
}
