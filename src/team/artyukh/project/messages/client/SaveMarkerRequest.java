package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class SaveMarkerRequest {
	private JSONObject request = new JSONObject();
	
	public SaveMarkerRequest(String markerId){
		try {
			request.put("type", "savemarker");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("id", markerId);
		} catch (JSONException e) {
		}
	}
	
	public void editInfo(String title, String description, String address, double lat, double lon){
		try {
			request.put("edit", true);
			request.put("title", title);
			request.put("description", description);
			request.put("address", address);
			request.put("lat", lat);
			request.put("lon", lon);
		} catch (JSONException e) {
		}
	}
	
	public void addImage(String image){	
		try {
			request.put("userid", BindingActivity.getStringPref(BindingActivity.PREF_USER_ID));
			request.put("object", ImageUploadRequest.OBJ_MARKER);
			request.put("image", image);
		} catch (JSONException e) {
		}
		
	}
	
	public String toString(){
		return request.toString();
	}
}
