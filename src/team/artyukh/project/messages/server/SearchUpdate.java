package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.Person;

public class SearchUpdate {
	private ArrayList<IListable> result;
	
	public SearchUpdate(JSONObject searchResult){
		try {
			result = getObjList(searchResult.getJSONArray("people"));
		} catch (JSONException e) {
		}
	}
	
	private ArrayList<IListable> getObjList(JSONArray arr){
		ArrayList<IListable> objects = new ArrayList<IListable>();
		IListable obj;
		for(int i = 0; i < arr.length(); i++){
			try {
				obj = new Person(arr.getJSONObject(i).getString("username"),
						arr.getJSONObject(i).getString("status"), 
						arr.getJSONObject(i).getString("_id"), 
						arr.getJSONObject(i).getString("picDate"),
						arr.getJSONObject(i).getString("online"));
				objects.add(obj);
				
			} catch (JSONException e) {
			}
		}
		
		return objects;
	}
	
	public ArrayList<IListable> getResult(){
		return result;
	}
}
