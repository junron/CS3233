module main {
  exports application;
  opens  application;

  requires javafx.base;
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.web;
  requires javafx.fxml;
}