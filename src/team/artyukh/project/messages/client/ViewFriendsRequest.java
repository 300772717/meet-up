package team.artyukh.project.messages.client;

import java.util.ArrayList;

import org.json.JSONArray;
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
	
	public ViewFriendsRequest(ArrayList<String> exceptions){
		this();
		
		JSONArray except = new JSONArray();
		try {
			for(String friendId : exceptions){
				except.put(friendId);
			}
			request.put("except", except);
		} catch (JSONException e) {
		}
		
		
	}
	
	public String toString(){
		return request.toString();
	}
}
