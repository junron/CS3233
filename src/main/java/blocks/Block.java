package blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

//MVC classification: M

/*
 * Block is one of the key classes in this program.
 * 
 * A block takes in a list of titles, and outputs a new list of titles.
 * This behavior is represented by String[] handle(String[] titles).
 * 
 * A block also has some variables, such as a regex pattern to compare against.
 * This is represented by EditableAttribute[] getAttributes() 
 * more will be explained on EditableAttribute in EditableAttribute.java.
 */

public abstract class Block {
	abstract String[] handle(String[] titles) throws InterruptedException;
	abstract EditableAttribute[] getAttributes();
	
	//JSON representation of a block, used to export blocks to a file.
	public final JsonObject toJson() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("blockType", getClass().getSimpleName());
		
		JsonArray attributes = new JsonArray();
		for (EditableAttribute attr : getAttributes()) {
			JsonObject attribute = new JsonObject();
			attribute.addProperty("name", attr.name);
			attribute.addProperty("type", attr.getClass().getSimpleName());
			attribute.addProperty("value", attr.valueGetter.get().toString());
			attributes.add(attribute);
		}
		
		jsonObj.add("attributes", attributes);
		return jsonObj;
	}
}
