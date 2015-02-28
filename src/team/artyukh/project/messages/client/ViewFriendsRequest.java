package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class ViewFriendsRequest {
	private JSONObject request = new JSONObject();
	
	public ViewFriendsRequest(){
		try {
			request.put("type", "viewfriends");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
