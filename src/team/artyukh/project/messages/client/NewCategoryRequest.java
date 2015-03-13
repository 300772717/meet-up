package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import team.artyukh.project.BindingActivity;

public class NewCategoryRequest {
	private JSONObject request = new JSONObject();
	
	public NewCategoryRequest(String catName){
		try {
			request.put("type", "newfriendcategory");
			request.put("title", catName);
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
		} catch (JSONException e) {
			Log.i("EXCEPTION", "NEWCAT");
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
