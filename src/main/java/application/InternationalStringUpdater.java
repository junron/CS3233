package application;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.StringProperty;

//MVC classification: C

public class InternationalStringUpdater {
	//list of simpleentries used instead of hashmap as one translation can correspond to 
	//more than one UI element. Format: <Name in .properties file, textProperty to update>
	private List<SimpleEntry<String, StringProperty>> updatableStrings;
	private ResourceBundle currentRB = null;
	
	InternationalStringUpdater(ResourceBundle defaultRB){
		updatableStrings = new ArrayList<SimpleEntry<String, StringProperty>>();
		currentRB = defaultRB;
	}
	
	public void add(String name, StringProperty prop) {
		updatableStrings.add(new SimpleEntry<String, StringProperty>(name, prop));
	}
	
	public void update() {
		//Iterate, set text to translated string.
		for (SimpleEntry<String, StringProperty> entry : updatableStrings) {
			String name = entry.getKey();
			StringProperty prop = entry.getValue();
			try {
				String translation = currentRB.getString(name).replace("\\n", "\n"); 
				prop.set(translation);
			} catch (Exception e) {
				prop.set(name+"_"+currentRB.getLocale().getLanguage());
				e.printStackTrace();
			}
		}
	}
	
	public void update(ResourceBundle rb) {
		currentRB=rb;
		update();
	}
}
