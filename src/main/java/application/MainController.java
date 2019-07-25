package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MainController implements Initializable {

  @FXML
  private AnchorPane parent;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GridPane grid = new GridPane();
    TextField[] topRow = new TextField[10];
    Random r = new Random();
    for(int i=0;i<10;i++){
      for(int j=0;j<10;j++){
        int val = r.nextInt(2);
        TextField t = new TextField(Integer.toString(val));
        t.setPrefHeight(40);
        t.setPrefWidth(40);
        if(i==0){
          topRow[j] = t;
        }else{
          int diff = j-i;
          if(diff>-1){
            t.textProperty().bind(topRow[diff].textProperty());
          }
        }
        grid.add(t,j,i);
      }
    }

    parent.getChildren().add(grid);
  }
}

