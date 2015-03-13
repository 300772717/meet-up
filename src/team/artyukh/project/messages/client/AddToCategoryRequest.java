package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class AddToCategoryRequest {
	private JSONObject request = new JSONObject();
	
	public AddToCategoryRequest(String catid, String friendid){
		try {
			request.put("type", "addtocategory");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("catid", catid);
			request.put("friendid", friendid);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
