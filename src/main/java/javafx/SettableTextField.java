package javafx;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

public class SettableTextField extends TextField {
  private String previousValue;
  private ChangeListener<String> changeListener;

  public SettableTextField() {
    this.textProperty().addListener((observable, _old, val) -> {
      if (val.equals(previousValue)) return;
      if (this.changeListener != null) this.changeListener.changed(observable, _old, val);
      this.previousValue = val;
    });
  }

  public void changeText(String text) {
    this.previousValue = text;
    this.setText(text);
  }

  public void setChangeListener(ChangeListener<String> changeListener) {
    this.changeListener = changeListener;
  }
}
