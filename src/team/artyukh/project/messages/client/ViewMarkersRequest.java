package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class ViewMarkersRequest {
	JSONObject request = new JSONObject();
	
	public ViewMarkersRequest(){
		try {
			request.put("type", "viewmarkers");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
