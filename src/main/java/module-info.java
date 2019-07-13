module tank.main {
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.swing;
  requires javafx.media;
  requires com.almasb.fxgl.all;
  exports application;
  opens application;
}