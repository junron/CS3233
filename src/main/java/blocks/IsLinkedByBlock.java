package blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import wikiAPI.API;

/*
 * Subclass of Block.
 */

public class IsLinkedByBlock extends Block {
	private String page = "";

	@Override
	String[] handle(String[] titles) {
		String[] linksTo = null;
		try {
			linksTo = API.links(page);
		} catch (IOException e) {e.printStackTrace();}
		
		HashSet<String> ret = new HashSet<String>();
		for (String links : linksTo) {
			for (String title : titles) {
				if (title.equalsIgnoreCase(links)) {
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
			new StringAttribute("Page", (String s)->setPage(s), ()-> {return getPage();})
		};
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public static IsLinkedByBlock jsonToBlock(JsonArray attributes) {
		IsLinkedByBlock ret = new IsLinkedByBlock();
		attributes.forEach((e)->{
			JsonObject obj = e.getAsJsonObject();
			switch (obj.get("name").getAsString()) {
			case "Page": ret.setPage(obj.get("value").getAsString()); break;
			}
		});
		return ret;
	}

}
