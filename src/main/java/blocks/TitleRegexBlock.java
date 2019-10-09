package blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import application.MainController;
import customInputFields.BooleanInput;
import customInputFields.StringInput;

/*
 * Subclass of Block.
 */

public class TitleRegexBlock extends Block{
	private String regexPattern = "";
	private boolean ignoreCase = true;

	public TitleRegexBlock() {}
	
	public TitleRegexBlock(Block block) {
		this(block, ".*");
	}

	public TitleRegexBlock(String pattern) {
		this(null, pattern);
	}
	
	public TitleRegexBlock(Block block, String pattern) {
		regexPattern = pattern;
	}

	@Override
	String[] handle(String[] titles) {
		HashSet<String> ret = new HashSet<String>();
		Pattern regex = null;
		try {
			if (ignoreCase) regex = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
			else regex = Pattern.compile(regexPattern);
		} catch (PatternSyntaxException e) {
			MainController.errorAlert("Invalid regex", e.getLocalizedMessage());
			return new String[0];
		}
		for (String s : titles) {
			if (regex.matcher(s).matches()) ret.add(s);
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

	public static TitleRegexBlock jsonToBlock(JsonArray attributes) {
		TitleRegexBlock ret = new TitleRegexBlock();
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
