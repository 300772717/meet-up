package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MapUpdate {
	
	private ArrayList<MapObject> people = new ArrayList<MapObject>();
	private ArrayList<MapObjectMarker> markers = new ArrayList<MapObjectMarker>();
	
	public MapUpdate(JSONObject mapUpdate){
		try {
			getObjList(mapUpdate.getJSONArray("people"), MapObject.TYPE_PERSON);
			getObjList(mapUpdate.getJSONArray("markers"), MapObject.TYPE_MARKER);
		} catch (JSONException e) {
		}
	}
	
	public ArrayList<MapObject> getPeople(){
		return people;
	}
	
	public ArrayList<MapObjectMarker> getMarkers(){
		return markers;
	}
	
	private void getObjList(JSONArray arr, int type){
		MapObject obj;
		MapObjectMarker mrk;
		
		for(int i = 0; i < arr.length(); i++){
			try {
				if(type == MapObject.TYPE_PERSON){
					obj = new MapObject(type, arr.getJSONObject(i).getDouble("lat"), arr.getJSONObject(i).getDouble("lon"), arr.getJSONObject(i).getString("_id"));
					people.add(obj);
				}
				else{
					mrk  = new MapObjectMarker(type,
							arr.getJSONObject(i).getDouble("lat"),
							arr.getJSONObject(i).getDouble("lon"),
							arr.getJSONObject(i).getString("_id"),
							arr.getJSONObject(i).getString("userid"),
							arr.getJSONObject(i).getString("description"),
							arr.getJSONObject(i).getString("picDate"));
					markers.add(mrk);
				}
				
				
				
			} catch (JSONException e) {
				Log.i("EX", e.toString());
			}
		}
	}

}
