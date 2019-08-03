package application;

import javafx.beans.property.*;

public class Population  {
	private final SimpleStringProperty year;
    private final SimpleIntegerProperty total;
    private final SimpleIntegerProperty  numOfMale, numOfFemale;
    
    

    public Population(String year, Integer total, Integer numOfMale, Integer numOfFemale) {
        this.year = new SimpleStringProperty(year);
        this.total = new SimpleIntegerProperty (total);
        this.numOfMale = new SimpleIntegerProperty (numOfMale);
        this.numOfFemale = new SimpleIntegerProperty (numOfFemale);
    }

    public String getYear() {
        return year.get();
    }


    public Integer  getTotal() {
        return total.get();
    }


    public Integer getNumOfMale() {
        return numOfMale.get();
    }
    
    public Integer getNumOfFemale() {
        return numOfFemale.get();
    }


}
