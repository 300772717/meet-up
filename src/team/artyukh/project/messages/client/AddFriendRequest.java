package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class AddFriendRequest {
	JSONObject request = new JSONObject();
	
	public AddFriendRequest(String userId){
		try {
			request.put("type", "addfriend");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("friendid", userId);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
