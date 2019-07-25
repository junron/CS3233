package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MainController implements Initializable {

  @FXML
  private AnchorPane parent;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GridPane grid = new GridPane();
    Random r = new Random();
    for(int i=0;i<10;i++){
      for(int j=0;j<10;j++){
        int val = r.nextInt(2);
        TextField t = new TextField(Integer.toString(val));
        t.setPrefHeight(40);
        t.setPrefWidth(40);
        grid.add(t,i,j);
      }
    }

    for(int i =0;i<10;i++){
      TextField top = grid.getC
    }

    parent.getChildren().add(grid);
  }
}

