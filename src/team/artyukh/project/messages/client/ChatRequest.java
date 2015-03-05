package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class ChatRequest {
	JSONObject request = new JSONObject();

	public ChatRequest(String message) {
		try {
			request.put("type", "chat");
			request.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("message", message);
		} catch (JSONException e) {
		}

	}

	public String toString() {
		return request.toString();
	}
}
