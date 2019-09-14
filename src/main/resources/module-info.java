module lightmoduleproj {
  requires javafx.fxml;
  requires javafx.controls;
  requires java.desktop;
  requires kotlin.stdlib;
  exports application;
  opens application;
}