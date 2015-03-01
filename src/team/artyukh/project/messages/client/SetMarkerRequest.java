package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class SetMarkerRequest {
	JSONObject request = new JSONObject();
	
	public SetMarkerRequest(double lat, double lon){
		try {
			request.put("type", "setmarker");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
			request.put("lat", lat);
			request.put("lon", lon);
		} catch (JSONException e) {
		}
	}
	
	public SetMarkerRequest(String markerId){
		try {
			request.put("type", "setmarker");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
			request.put("markerid", markerId);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
