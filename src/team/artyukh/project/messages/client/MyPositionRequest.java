package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class MyPositionRequest {
	JSONObject request = new JSONObject();

	public MyPositionRequest(double lon, double lat) {
		try {
			request.put("type", "id");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("lat", lat);
			request.put("lon", lon);
		} catch (JSONException e) {
		}

	}

	public String toString() {
		return request.toString();
	}
}
