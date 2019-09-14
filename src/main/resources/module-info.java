module lightmoduleproj {
  requires javafx.fxml;
  requires javafx.controls;
  requires java.desktop;
  exports application;
  opens application;
}