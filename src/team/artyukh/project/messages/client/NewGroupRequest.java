package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class NewGroupRequest {
	JSONObject request = new JSONObject();

	public NewGroupRequest(){
		try {
			request.put("type", "newgroup");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
