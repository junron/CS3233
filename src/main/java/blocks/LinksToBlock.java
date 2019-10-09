package blocks;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import wikiAPI.API;
import wikiAPI.MultiThreadedAPI;

/*
 * Subclass of Block.
 */

public class LinksToBlock extends Block {
	private String page = "";

	@Override
	String[] handle(String[] titles) throws InterruptedException {
		Map<String, String[]> extracts = MultiThreadedAPI.linksSeperated(titles, 200);
		
		HashSet<String> ret = new HashSet<String>();
		for (String title : titles) {
			for (String link : extracts.get(title)) {
				for (String temp : page.split("\\|")) {
					if (link.equals(temp)) {
						ret.add(title);
						break;
					}
				}
				if (ret.contains(title)) break;
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

	public static LinksToBlock jsonToBlock(JsonArray attributes) {
		LinksToBlock ret = new LinksToBlock();
		attributes.forEach((e)->{
			JsonObject obj = e.getAsJsonObject();
			switch (obj.get("name").getAsString()) {
			case "Page": ret.setPage(obj.get("value").getAsString()); break;
			}
		});
		return ret;
	}

}
