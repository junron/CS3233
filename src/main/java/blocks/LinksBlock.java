package blocks;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import customInputFields.BooleanInput;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import wikiAPI.MultiThreadedAPI;

/*
 * Subclass of Block.
 */

public class LinksBlock extends Block{
	private boolean additive = true;

	@Override
	String[] handle(String[] titles) throws InterruptedException {
		Set<String> result = new HashSet();
		for (String s : MultiThreadedAPI.links(titles, 200)) 
			result.add(s);
		
		if (additive) for (String s : titles)
			result.add(s);

		return result.toArray(new String[0]);
	}

	public boolean isAdditive() {
		return additive;
	}

	public void setAdditive(boolean additive) {
		this.additive = additive;
	}
	
	@Override
	EditableAttribute[] getAttributes() {
		return new EditableAttribute[] {
			new BooleanAttribute("Additive",(Boolean b) -> setAdditive(b),() -> {return isAdditive();})
		};
	}

	public static LinksBlock jsonToBlock(JsonArray attributes) {
		LinksBlock ret = new LinksBlock();
		attributes.forEach((e)->{
			JsonObject obj = e.getAsJsonObject();
			switch (obj.get("name").getAsString()) {
			case "Additive": ret.setAdditive(obj.get("value").getAsString().equals("true")); break;
			}
		});
		return ret;
	}
	
}
