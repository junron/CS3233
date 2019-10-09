package blocks;

import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.beans.property.SimpleStringProperty;
import wikiAPI.MultiThreadedAPI;

/*
 * Subclass of Block.
 */

public class FromCategoryBlock extends Block {
	private String category = "";
	private int depth = 5;

	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	String[] handle(String[] titles) throws InterruptedException {
		//preprocessing of user input
		titles = category.split("\\|");
		for (int i = 0; i<titles.length; i++) {
			titles[i] = IsInCategoryBlock.getCategoryLang().getValue() + ":" + titles[i];
			System.out.println(titles[i]);
		}
		
		HashSet<String> ret = new HashSet<String>();
		for (int count = 0; count < depth; count++) {
			for (String title : MultiThreadedAPI.fromCategories(titles, 200))
				ret.add(title);
			titles = MultiThreadedAPI.subCategories(titles, 200);
		}
		
		return ret.toArray(new String[0]);
	}

	@Override
	EditableAttribute[] getAttributes() {
		return new EditableAttribute[] {
				new StringAttribute("Category",(String s) -> setCategory(s), () -> {return getCategory();}),
				new IntAttribute("Depth",(Integer i) -> setDepth(i), () -> {return getDepth();})
		};
	}

	public static FromCategoryBlock jsonToBlock(JsonArray attributes) {
		FromCategoryBlock ret = new FromCategoryBlock();
		attributes.forEach((e)->{
			JsonObject obj = e.getAsJsonObject();
			switch (obj.get("name").getAsString()) {
			case "Category": ret.setCategory(obj.get("value").getAsString()); break;
			case "Depth": ret.setDepth(Integer.parseInt(obj.get("value").getAsString())); break;
			}
		});
		return ret;
	}
}