module main {
  opens models;
  opens application;
  requires javafx.controls;
  requires javafx.fxml;
  exports application;
}
