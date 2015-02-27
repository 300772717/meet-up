package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class InviteRequest {
	JSONObject request = new JSONObject();

	public InviteRequest(String invitee){
		try {
			request.put("type", "invite");
			request.put("inviteuser", invitee);
			request.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			request.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return request.toString();
	}
}
