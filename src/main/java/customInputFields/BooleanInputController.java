package customInputFields;

import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

//MVC classification: C

public class BooleanInputController {
	@FXML
	private Label nameLBL;
	@FXML
	private CheckBox checkbox;

	public Label getNameLBL() {
		return nameLBL;
	}
	public CheckBox getCheckbox() {
		return checkbox;
	}
}
