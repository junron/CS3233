package customInputFields;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

//Wrapper for BooleanInput.fxml and BooleanInputController
//Displays a name and a checkbox in "Edit block variables" section.
//MVC classification: C

public class BooleanInput {
	private Pane pane;
	private BooleanInputController controller;
	
	public BooleanInput() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/BooleanInput.fxml"));
			pane = loader.load();
			controller = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Pane getPane() {
		return pane;
	}
	
	public BooleanProperty getBooleanProperty() {
		return controller.getCheckbox().selectedProperty();
	}
	
	public void setName(String name) {
		controller.getNameLBL().setText(name);
	}
	
	public void setSelected(Boolean selected) {
		controller.getCheckbox().setSelected(selected);
	}
}
