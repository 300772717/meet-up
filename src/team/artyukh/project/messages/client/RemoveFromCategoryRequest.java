package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class RemoveFromCategoryRequest {
	private JSONObject request = new JSONObject();
	
	public RemoveFromCategoryRequest(String catId, String friendId){
		try {
			request.put("type", "removefromcategory");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("friendid", friendId);
			request.put("catid", catId);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
