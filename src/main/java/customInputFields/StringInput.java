package customInputFields;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

//Wrapper for StringInput.fxml and StringInputController
//Displays a name and a textfield in "Edit block variables" section.
//MVC classification: C

public class StringInput {
	private Pane pane;
	private StringInputController controller;
	
	public StringInput() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/StringInput.fxml"));
			pane = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Pane getPane() {
		return pane;
	}
	
	public StringProperty getStringProperty() {
		return controller.getFieldTXT().textProperty();
	}
	
	public void setName(String name) {
		controller.getNameLBL().setText(name);
	}
	
	public void setValue(String val) {
		controller.getFieldTXT().setText(val);
	}
}
