package application;

import javafx.beans.binding.StringBinding;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

class TextDisplay{
  private final Text mainText;
  private final Text titleText;


  TextDisplay(String title,double posX,double posY){
    titleText = new Text();
    titleText.setFont(Font.font(18));
    titleText.setText(title);
    titleText.setTranslateX(posX-50);
    titleText.setTranslateY(posY-50);

    this.mainText = new Text();
    mainText.setFont(Font.font(28));
    mainText.setTranslateX(posX);
    mainText.setTranslateY(posY);
    getGameScene().addUINodes(this.mainText,this.titleText);
  }

  void bind(StringBinding binding){
    this.mainText.textProperty().bind(binding);
  }


}
