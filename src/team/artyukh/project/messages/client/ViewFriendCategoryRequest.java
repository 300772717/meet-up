package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class ViewFriendCategoryRequest {
	private JSONObject request = new JSONObject();
	
	public ViewFriendCategoryRequest(String catId){
		try {
			request.put("type", "viewfriendcategory");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("catid", catId);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
