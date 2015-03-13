package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRequest {
	JSONObject request = new JSONObject();

	public LoginRequest(String username, String password) {
		try {
			request.put("type", "login");
			request.put("username", username);
			request.put("password", password);
		} catch (JSONException e) {
		}

	}

	public String toString() {
		return request.toString();
	}
}
