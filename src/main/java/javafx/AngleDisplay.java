package javafx;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class AngleDisplay extends GridPane {

  private ArrayList<Pair> data = new ArrayList<>();

  public AngleDisplay(String... data) {
    int i = 0;
    String key = "";
    for (String s : data) {
      if (i % 2 == 0) {
        key = s;
      } else {
        this.data.add(new Pair(key, s));
      }
      i++;
    }
    renderRows();
  }

  private void renderRows() {
    this.getChildren().removeAll(this.getChildren());
    int rowNum = 0;
    for (Pair pair : data) {
      Text title = new Text(pair.getA() + ": ");
      title.setStyle("-fx-font-weight: bold");

      Text value = new Text(pair.getB());

      this.add(title, 0, rowNum);
      this.add(value, 1, rowNum);
      rowNum++;
    }
  }

  static class Pair {
    private String a, b;

    Pair(String a, String b) {
      this.a = a;
      this.b = b;
    }

    public String getA() {
      return a;
    }

    public String getB() {
      return b;
    }
  }

}

