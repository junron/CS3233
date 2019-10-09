package blocks;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import application.MainController;
import wikiAPI.API;

public class LinksFromBlock extends Block {
	private String page = "";

	@Override
	String[] handle(String[] titles) throws InterruptedException {
		try {
			return API.linksHere(page);
		} catch (IOException e) {
			MainController.errorAlert("IO Error", e.getLocalizedMessage());
			return new String[0];
		}
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

	public static LinksFromBlock jsonToBlock(JsonArray attributes) {
		LinksFromBlock ret = new LinksFromBlock();
		attributes.forEach((e)->{
			JsonObject obj = e.getAsJsonObject();
			switch (obj.get("name").getAsString()) {
			case "Page": ret.setPage(obj.get("value").getAsString()); break;
			}
		});
		return ret;
	}

}
