package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class RemoveMarkerRequest {
	private JSONObject request = new JSONObject();
	
	public RemoveMarkerRequest(String markerId){
		try {
			request.put("type", "removemarker");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("id", markerId);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
