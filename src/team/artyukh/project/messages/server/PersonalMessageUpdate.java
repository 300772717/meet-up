package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PersonalMessageUpdate {
	private String message;
	private String from;

	public PersonalMessageUpdate(JSONObject msgUpdate) {
		try {
			this.message = msgUpdate.getString("text");
			this.from = msgUpdate.getString("username");
		} catch (JSONException e) {
			Log.i("EX CHAT", e.toString());
		}
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return from;
	}
}
