package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MapUpdate {
	
	private ArrayList<MapObject> people;
	private ArrayList<MapObject> markers;
	
	public MapUpdate(JSONObject mapUpdate){
		try {
			people = getObjList(mapUpdate.getJSONArray("people"), "person");
//			markers = getObjList(mapUpdate.getJSONArray("markers"), "marker");
		} catch (JSONException e) {
		}
	}
	
	public ArrayList<MapObject> getPeople(){
		return people;
	}
	
	public ArrayList<MapObject> getMarkers(){
		return markers;
	}
	
	private ArrayList<MapObject> getObjList(JSONArray arr, String type){
		ArrayList<MapObject> objects = new ArrayList<MapObject>();
		MapObject obj;
		
		for(int i = 0; i < arr.length(); i++){
			try {
				obj = new MapObject(type, arr.getJSONObject(i).getDouble("lat"), arr.getJSONObject(i).getDouble("lon"), arr.getJSONObject(i).getString("_id"));
				
				objects.add(obj);
				
			} catch (JSONException e) {
				Log.i("EX", e.toString());
			}
		}
		
		return objects;
	}

}
