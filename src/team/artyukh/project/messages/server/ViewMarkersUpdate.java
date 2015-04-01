package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.MapMarker;

public class ViewMarkersUpdate {
	private ArrayList<IListable> markers = new ArrayList<IListable>();
	
	public ViewMarkersUpdate(JSONObject update){
		try {
			JSONArray mrkArr = update.getJSONArray("markers");
			
			for(int i = 0; i < mrkArr.length(); i++){
				JSONObject marker = mrkArr.getJSONObject(i);
				MapMarker mMark = new MapMarker(
						marker.getString("title"), 
						marker.getString("description"), 
						marker.getString("id"), 
						marker.getString("picDate"),
						marker.getString("address"),
						marker.getBoolean("current"));
				markers.add(mMark);
			}
		} catch (JSONException e) {
		}
	}
	
	public ArrayList<IListable> getMarkers(){
		return markers;
	}
}
