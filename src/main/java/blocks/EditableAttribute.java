package blocks;

import customInputFields.StringInput;
import customInputFields.BooleanInput;
import javafx.scene.layout.Pane;

/*
 * EditableAttribute and its subclasses are one of the key classes in this program, doing the following:
 * 1. Representation of block variables in an object, thus the reference to the block
 *    is not needed in order to modify its variables.
 * 2. Updates UI to allow user to have a visual interface when editing variables in "Edit block variables" section.
 * 3. Contains logic of getting values from UI textfield, then checking validity and 
 *    finally updating the value of the block variable.
 * MVC classification: C
 */

public abstract class EditableAttribute<T> {
	protected String name;
	//functional interfaces to set and get block variables
	protected ValueSetter<T> valueSetter;
	protected ValueGetter<T> valueGetter;
	
	public EditableAttribute (String name,  ValueSetter<T> valueSetter, ValueGetter<T> valueGetter){
		this.name = name;
		this.valueSetter = valueSetter;
		this.valueGetter = valueGetter;
	}
	
	public abstract Pane getEditPane();
}

//Used for an integer block variable, such as recursion depth.
class IntAttribute extends EditableAttribute<Integer>{
	public IntAttribute(String name, ValueSetter<Integer> valueSetter, ValueGetter<Integer> valueGetter) {
		super(name, valueSetter, valueGetter);
	}

	@Override
	public Pane getEditPane() {
		StringInput nfp = new StringInput();
		nfp.setName(name);
		nfp.setValue(valueGetter.get().toString());
		nfp.getStringProperty().addListener((observable, oldValue, newValue)->{
			if (newValue.isBlank()) {
				nfp.getStringProperty().set("0");
				valueSetter.set(0);
				return;
			}
			int val;
			try {
				val = Integer.parseInt(newValue);
				valueSetter.set(val);
			} catch (Exception e) {
				nfp.getStringProperty().set(oldValue);
				return;
			}
		});
		return nfp.getPane();
	}
}

//Used for a string block variable, such as regex pattern.
class StringAttribute extends EditableAttribute<String>{

	public StringAttribute(String name, ValueSetter<String> valueSetter, ValueGetter<String> valueGetter) {
		super(name, valueSetter, valueGetter);
	}

	@Override
	public Pane getEditPane() {
		StringInput stringInput = new StringInput();
		stringInput.setName(name);
		stringInput.setValue(valueGetter.get().toString());
		stringInput.getStringProperty().addListener((observable, oldValue, newValue)->{
			valueSetter.set(newValue);
		});
		return stringInput.getPane();
	}
}

//Used for a boolean block variable, such as case sensitivity.
class BooleanAttribute extends EditableAttribute<Boolean>{
	public BooleanAttribute(String name, ValueSetter<Boolean> valueSetter, ValueGetter<Boolean> valueGetter) {
		super(name, valueSetter, valueGetter);
	}

	@Override
	public Pane getEditPane() {
		BooleanInput boolInput = new BooleanInput();
		boolInput.setName(name);
		boolInput.setSelected((Boolean)valueGetter.get());
		boolInput.getBooleanProperty().addListener((observable, oldValue, newValue)->{	
			valueSetter.set(newValue);
		});
		return boolInput.getPane();
	}
	
}

@FunctionalInterface
interface ValueSetter<T>{
	public void set(T newValue);
}

@FunctionalInterface
interface ValueGetter<T>{
	public T get();
}