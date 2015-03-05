package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class UpdateGroupRequest {
	JSONObject request = new JSONObject();

	public UpdateGroupRequest(String groupId) {
		try {
			request.put("type", "updategroup");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("group", groupId);
		} catch (JSONException e) {
		}

	}

	public String toString() {
		return request.toString();
	}
}
