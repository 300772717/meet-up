package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class ViewGroupInfoRequest {
	private JSONObject request = new JSONObject();
	
	public ViewGroupInfoRequest(){
		try {
			request.put("type", "viewgroupinfo");
			request.put("id", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}