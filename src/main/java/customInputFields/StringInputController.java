package customInputFields;

import javafx.fxml.FXML;

import javafx.scene.control.TextField;

import javafx.scene.control.Label;

//MVC classification: C

public class StringInputController {
	@FXML
	private Label nameLBL;
	@FXML
	private TextField FieldTXT;

	public Label getNameLBL() {
		return nameLBL;
	}
	public TextField getFieldTXT() {
		return FieldTXT;
	}
	
}
