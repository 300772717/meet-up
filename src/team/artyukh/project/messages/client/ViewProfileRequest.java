package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewProfileRequest {
	private JSONObject request = new JSONObject();
	
	public ViewProfileRequest(String userId){
		try {
			request.put("type", "viewprofile");
			request.put("id", userId);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
