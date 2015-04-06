package team.artyukh.project.messages.client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.messages.server.MapObject;

public class SearchRequest {
	private JSONObject query = new JSONObject();
	private JSONArray people = new JSONArray();
	
	public SearchRequest(String username){
		addPerson("username", username);
		makeQuery();
	}
	
	public SearchRequest(ArrayList<MapObject> objects){
		for(MapObject obj : objects){
			if(obj.getType() == MapObject.TYPE_PERSON){
				addPerson("_id", obj.getId());
			}
		}
		makeQuery();
	}
	
	public String toString(){
		return query.toString();
	}
	
	private void addPerson(String tag, String val){
		try {
			people.put(new JSONObject().put(tag, val));
		} catch (JSONException e) {
		}
	}
	
	private void makeQuery(){
		try {
			query.put("type", "search");
			query.put("people", people);
		} catch (JSONException e) {
		}
	}
	
}
