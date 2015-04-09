package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class EditGroupRequest {
	private JSONObject request = new JSONObject();
	
	public EditGroupRequest(String name, String purpose, String address){
		try {
			request.put("type", "editgroup");
			request.put("id", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
			request.put("name", name);
			request.put("purpose", purpose);
			request.put("address", address);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
