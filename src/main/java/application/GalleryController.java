package application;

import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import models.cars.Car;
import models.cars.Economy;
import storage.CarStorage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GalleryController implements Initializable {
  public Pagination pagination;
  private ArrayList<Car> economyCars;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    economyCars = CarStorage.storage.filter(car->car instanceof Economy);
    pagination.setPageCount(economyCars.size());
    pagination.setPageFactory(index -> {
      return economyCars.get(index).getImage();
    });
  }
}
