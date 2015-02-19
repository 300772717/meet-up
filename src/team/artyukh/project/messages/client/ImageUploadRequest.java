package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import team.artyukh.project.BindingActivity;

public class ImageUploadRequest {
	private JSONObject request = new JSONObject();
	public static final String OBJ_PERSON = "person";
	public static final String OBJ_MARKER = "marker";	
	
	public ImageUploadRequest(String object, String image){
		try {
			request.put("type", "image");
			request.put("object", object);
			request.put("id", BindingActivity.getStringPref(BindingActivity.PREF_USER_ID));
			request.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
			request.put("image", image);
		} catch (JSONException e) {
			Log.i("REQUEST OBJECT", "EXCEPTION");
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
