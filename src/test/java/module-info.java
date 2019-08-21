module main {
  exports paTest;
  opens paTest;
  requires junit;
  requires org.testfx;
  requires org.testfx.junit;
  requires javafx.graphics;
  requires javafx.fxml;
  requires javafx.controls;
}