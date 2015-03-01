package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class SaveMarkerRequest {
	private JSONObject request = new JSONObject();
	
	public SaveMarkerRequest(String markerId){
		try {
			request.put("type", "savemarker");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("id", markerId);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
