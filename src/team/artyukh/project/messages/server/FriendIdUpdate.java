package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendIdUpdate {
	ArrayList<String> friendIds = new ArrayList<String>();
	
	public FriendIdUpdate(JSONObject update){
		try {
			JSONArray idArr = update.getJSONArray("friends");
			
			for(int i = 0; i < idArr.length(); i++){
				friendIds.add(idArr.getString(i));
			}
		} catch (JSONException e) {
		}
	}
	
	public ArrayList<String> getFriendIds(){
		return friendIds;
	}
}
