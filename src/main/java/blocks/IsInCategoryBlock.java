package blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.beans.property.SimpleStringProperty;
import wikiAPI.MultiThreadedAPI;

/*
 * Subclass of Block.
 */

public class IsInCategoryBlock extends Block {
	private static SimpleStringProperty categoryLang = new SimpleStringProperty("Category");
	private String category = "";
	private int depth = 5;
	
	public static SimpleStringProperty getCategoryLang() {
		return categoryLang;
	}
	
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
	String[] handle(String[] titles) throws InterruptedException{
		Set<String> categories = new HashSet<String>();
		Set<String> catTemp = new HashSet<String>();
		String[] split = category.split("\\|");
		for (int i = 0; i<split.length; i++) {
			catTemp.add(IsInCategoryBlock.getCategoryLang().getValue() + ":" + split[i]);
		}
		String[] cats = catTemp.toArray(new String[0]);
		
		for (int count = 0; count<depth; count++) {
			System.out.println(count+":"+cats.length);
			cats = MultiThreadedAPI.subCategories(cats, 200);
			System.out.println(cats.length);
			ArrayList<String> temp = new ArrayList<String>();
			for (String s : cats) {
				categories.add(s);
				temp.add(s);
			}
			cats = temp.toArray(new String[0]);
		}
		System.out.println(categories);
		Set<String> ret = new HashSet<String>();
		System.out.println("sep "+titles.length); 
		Map<String, String[]> sep = MultiThreadedAPI.superCategoriesSeperated(titles, 200);
		
		for (String title : titles) {
			for (String cat : sep.get(title)) {
				if (categories.contains(cat)) {
					ret.add(title);
					break;
				}
			}
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

	public static IsInCategoryBlock jsonToBlock(JsonArray attributes) {
		IsInCategoryBlock ret = new IsInCategoryBlock();
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
