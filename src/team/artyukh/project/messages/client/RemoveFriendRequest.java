package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class RemoveFriendRequest {
	JSONObject request = new JSONObject();

	public RemoveFriendRequest(String userId) {
		try {
			request.put("type", "removefriend");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("friendid", userId);
		} catch (JSONException e) {
		}
	}

	public String toString() {
		return request.toString();
	}
}
