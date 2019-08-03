package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
  @FXML
  private TableView<Person> tableView;
  @FXML
  private TableColumn<Person, String> firstNameColumn;
  @FXML
  private TableColumn<Person, String> lastNameColumn;
  @FXML
  private TableColumn<Person, String> emailColumn;
  @FXML
  private TextField firstNameTF;
  @FXML
  private TextField lastNameTF;
  @FXML
  private TextField emailTF;
  @FXML
  private Button addButton;

  //create initial data
  final ObservableList<Person> data = FXCollections.observableArrayList(
          new Person("Jacob", "Smith", "jacob.smith@example.com"),
          new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
          new Person("Ethan", "Williams", "ethan.williams@example.com"),
          new Person("Emma", "Jones", "emma.jones@example.com"),
          new Person("Michael", "Brown", "michael.brown@example.com")
  );

  public void initialize(URL location, ResourceBundle resources) {
    firstNameColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
    lastNameColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
    emailColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
    tableView.setItems(data);
  }

  // Event Listener on Button[#addButton].onAction
  @FXML
  public void addButtonAction(ActionEvent event) {
    data.add(new Person(
            firstNameTF.getText(),
            lastNameTF.getText(),
            emailTF.getText()
    ));
    firstNameTF.clear();
    lastNameTF.clear();
    emailTF.clear();
    tableView.setItems(data);
  }

  public void editData(ActionEvent actionEvent) {
    Person person = tableView.getSelectionModel().getSelectedItem();
    int index = data.indexOf(person);
    person.setEmail(emailTF.getText());
    person.setFirstName(firstNameTF.getText());
    person.setLastName(lastNameTF.getText());
    data.set(index,person);
  }

  public void deleteData(ActionEvent actionEvent) {
    Person person = tableView.getSelectionModel().getSelectedItem();
    data.remove(person);
  }
}