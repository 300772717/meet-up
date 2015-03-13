package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterRequest {
	JSONObject request = new JSONObject();

	public RegisterRequest(String username, String password) {
		try {
			request.put("type", "register");
			request.put("username", username);
			request.put("password", password);
		} catch (JSONException e) {
		}

	}

	public String toString() {
		return request.toString();
	}
}
