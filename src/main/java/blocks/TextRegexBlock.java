package blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import application.MainController;
import wikiAPI.API;
import wikiAPI.MultiThreadedAPI;

/*
 * Subclass of Block.
 */

public class TextRegexBlock extends Block {
	private String regexPattern = "";
	private boolean ignoreCase = true;

	@Override
	String[] handle(String[] titles) throws InterruptedException {
		Set<String> ret = new HashSet<String>();
		Map<String, String> extracts = MultiThreadedAPI.extractsSeperated(titles, 200);
		
		Pattern regex = null;
		try {
			if (ignoreCase) regex = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
			else regex = Pattern.compile(regexPattern);
		} catch (PatternSyntaxException e) {
			MainController.errorAlert("Invalid regex", e.getLocalizedMessage());
			return new String[0];
		}
		
		for (String title : titles) {
			String extract = extracts.get(title);
			if (extract != null && regex.matcher(extract).find()) {
				ret.add(title);
			}
		}
		return ret.toArray(new String[0]);
	}
	
	public String getRegexPattern() {
		return this.regexPattern;
	}

	public void setRegexPattern(String regexPattern) {
		this.regexPattern = regexPattern;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	@Override
	EditableAttribute[] getAttributes() {
		return new EditableAttribute[] {
			new StringAttribute("Regex pattern",(String s)->setRegexPattern(s),()->{return getRegexPattern();}),
			new BooleanAttribute("Ignore case",(Boolean b)->setIgnoreCase(b),()->{return isIgnoreCase();})
		};
	}
	
	public static TextRegexBlock jsonToBlock(JsonArray attributes) {
		TextRegexBlock ret = new TextRegexBlock();
		attributes.forEach((e)->{
			JsonObject obj = e.getAsJsonObject();
			switch (obj.get("name").getAsString()) {
			case "Regex pattern": ret.setRegexPattern(obj.get("value").getAsString()); break;
			case "Ignore case": ret.setIgnoreCase(obj.get("value").getAsString().equals("true")); break;
			}
		});
		return ret;
	}
}
