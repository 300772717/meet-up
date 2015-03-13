package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class PersonalMessageRequest {
	private JSONObject request = new JSONObject();
	
	public PersonalMessageRequest(String text, String username){
		try {
			request.put("type", "personalmessage");
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("to", username);
			request.put("text", text);
		} catch (JSONException e) {
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
