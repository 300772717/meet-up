package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class RemoveCategoryRequest {
	private JSONObject request = new JSONObject();
	
	public RemoveCategoryRequest(String id){
		try {
			request.put("type", "removefriendcategory");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("catid", id);
		} catch (JSONException e) {
		}
		
	}
	
	public String toString(){
		return request.toString();
	}
}
