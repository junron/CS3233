module main {
  opens models.cars;
  opens application;
  requires javafx.controls;
  requires javafx.fxml;
  exports application;
  exports models.cars;
}
