package wikiAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.*;

/*
 * API is one of the key classes in this program, doing the following:
 * 1. Makes all requests to the wikipedia API (wikipedia.org/api.php)
 * 2. Gets json data back and parses it
 * 
 * There is not much else to comment in this class,
 * most/all of it is constructing an API request based on function arguments,
 * sending the request and reading the data that comes back.
 * MVC classification: M
 */

public class API {
	private static String WIKI = "https://en.wikipedia.org";
	private final static JsonParser JSON = new JsonParser();
	
	public static void setWikiLang(String language) {
		WIKI = "https://"+language.substring(0,2)+".wikipedia.org";
	}
	
	public static String getWikiURL() {
		return WIKI;
	}
	
	public static String extracts(String title) throws IOException {
		JsonObject response = query("extracts&explaintext=1", title);
		response = response.get("query").getAsJsonObject();
		response = response.get("pages").getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : response.entrySet()) {
		    return entry.getValue().getAsJsonObject().get("extract").getAsString();
		}
		return null;
	}
	
	private static ArrayList<String> getLinksRecursive(String title, String plcontinue, ArrayList<String> history) throws IOException {
		String prop = "links" + "&pllimit=max" + "&plnamespace=0";
		if (plcontinue != null) prop += "&plcontinue="+plcontinue;
		JsonObject response = query(prop, title);
		try {
			plcontinue = response.get("continue").getAsJsonObject().get("plcontinue").getAsString();
		} catch (NullPointerException e) {
			plcontinue = null;
		}
		
		response = response.get("query").getAsJsonObject();
		response = response.get("pages").getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : response.entrySet()) {
			try {
			    for (JsonElement element : entry.getValue().getAsJsonObject().get("links").getAsJsonArray()) {
			    	history.add(element.getAsJsonObject().get("title").getAsString());
			    }
			} catch (NullPointerException e) {
				continue;
			}
		}
		if (plcontinue != null) return getLinksRecursive(title, plcontinue, history);
		return history;
	}
	
	public static String[] links(String title) throws IOException {
		//enter recursion
		return getLinksRecursive(title, null, new ArrayList<String>()).toArray(new String[0]);
	}
	
	private static ArrayList<String> getLinksHereRecursive(String title, String lhcontinue, ArrayList<String> history) throws IOException {
		String prop = "linkshere" + "&lhlimit=max" + "&lhnamespace=0";
		if (lhcontinue != null) prop += "&lhcontinue="+lhcontinue;
		JsonObject response = query(prop, title);
		try {
			lhcontinue = response.get("continue").getAsJsonObject().get("lhcontinue").getAsString();
		} catch (NullPointerException e) {
			lhcontinue = null;
		}
		
		response = response.get("query").getAsJsonObject();
		response = response.get("pages").getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : response.entrySet()) {
			try {
			    for (JsonElement element : entry.getValue().getAsJsonObject().get("linkshere").getAsJsonArray()) {
			    	history.add(element.getAsJsonObject().get("title").getAsString());
			    }
			} catch (NullPointerException e) {
				continue;
			}
		}
		if (lhcontinue != null) return getLinksHereRecursive(title, lhcontinue, history);
		return history;
	}
	
	public static String[] linksHere(String title) throws IOException {
		//enter recursion
		return getLinksHereRecursive(title, null, new ArrayList<String>()).toArray(new String[0]);
	}
	
	private static ArrayList<String> getCategoryMembersRecursive(String title, String cmcontinue, ArrayList<String> history, String cmtype) throws IOException {
		String requestStr = "action=query&format=json&list=categorymembers&cmtype=subcat&cmprop=title&cmlimit=max&cmshow=!hidden"+"&cmtype="+cmtype;
		requestStr += "&cmtitle=" + URLEncoder.encode(title, "UTF-8");
		if (cmcontinue != null) requestStr += "&cmcontinue="+cmcontinue;
		JsonObject response = request(requestStr);
		try {
			cmcontinue = response.get("continue").getAsJsonObject().get("cmcontinue").getAsString();
		} catch (NullPointerException e) {
			cmcontinue = null;
		}
		response = response.get("query").getAsJsonObject();
		for(JsonElement element : response.get("categorymembers").getAsJsonArray()) {
			try {
				history.add(element.getAsJsonObject().get("title").getAsString());
			} catch (NullPointerException e) {
				continue;
			}
		}
		if (cmcontinue != null) return getCategoryMembersRecursive(title, cmcontinue, history, cmtype);
		return history;
	}
	
	private static String[] categoryMembers(String title, String cmtype) throws IOException{
		return getCategoryMembersRecursive(title, null, new ArrayList<String>(), cmtype).toArray(new String[0]);
	}
	
	public static String[] subCategories(String title) throws IOException{
		return categoryMembers(title, "subcat");
	}
	
	public static String[] fromCategory(String title) throws IOException{
		return categoryMembers(title, "page");
	}
	
	private static ArrayList<String> getSuperCategoriesRecursive(String title, String clcontinue, ArrayList<String> history) throws IOException {
		String prop = "categories" + "&cllimit=max" + "&clshow=!hidden";
		if (clcontinue != null) prop += "&clcontinue="+clcontinue;
		JsonObject response = query(prop, title);
		try {
			clcontinue = response.get("continue").getAsJsonObject().get("clcontinue").getAsString();
		} catch (NullPointerException e) {
			clcontinue = null;
		}
		
		response = response.get("query").getAsJsonObject();
		response = response.get("pages").getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : response.entrySet()) {
			try {
			    for (JsonElement element : entry.getValue().getAsJsonObject().get("categories").getAsJsonArray()) {
			    	history.add(element.getAsJsonObject().get("title").getAsString());
			    }
			} catch (NullPointerException e) {
				continue;
			}
		}
		if (clcontinue != null) return getSuperCategoriesRecursive(title, clcontinue, history);
		return history;
	}
	
	public static String[] superCategories(String title) throws IOException {
		//enter recursion
		return getSuperCategoriesRecursive(title, null, new ArrayList<String>()).toArray(new String[0]);
	}
	
	//Does not guarantee page exists
	public static String fixTitle(String title) throws IOException {
		JsonObject response = query("", title);
		System.out.println(response);
		String retStr;
		response = response.get("query").getAsJsonObject();
		try {
			response = response.get("normalized").getAsJsonArray().get(0).getAsJsonObject();
			retStr = response.get("to").getAsString();
		} catch (NullPointerException e) {
			//Already normalized
			retStr = title;
		}
		
		return retStr;
	}
	
	public static JsonObject query(String prop, String title) throws IOException{
		title = URLEncoder.encode(title, "UTF-8");
		return request("action=query&format=json&prop="+prop+"&titles="+title);
	}
	
	public static JsonObject request(String queryString) throws IOException {
		String strURL = WIKI+"/w/api.php?"+queryString;
		URL url = new URL(strURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		if (conn.getResponseCode() == 429) {
			//System.err.println("Rate limited! Sleeping 10s...");
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return request(queryString);
			//try again after 10s sleep
		}
		
		else if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		String outputStr = "";
		while ((output = br.readLine()) != null) {
			outputStr += output;
		}
		conn.disconnect();
		
		return JSON.parse(outputStr).getAsJsonObject();
	}
}

