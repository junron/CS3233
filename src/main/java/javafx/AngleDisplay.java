package javafx;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.Map;

public class AngleDisplay extends GridPane {
  private Map<String, String> data;

  public AngleDisplay(Map<String, String> data) {
    this.data = data;
    renderRows();
  }

  public void renderRows() {
    this.getChildren().removeAll(this.getChildren());
    int rowNum = 0;
    for (Map.Entry<String,String> entry : data.entrySet()) {
      Text title = new Text(entry.getKey() +": ");
      title.setStyle("-fx-font-weight: bold");

      Text value = new Text(entry.getValue());

      this.add(title,0,rowNum);
      this.add(value,1,rowNum);
      rowNum++;
    }
  }

  public Map<String, String> getData() {
    return data;
  }
}
