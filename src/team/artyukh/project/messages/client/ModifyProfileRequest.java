package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class ModifyProfileRequest {
	
	private JSONObject clientRequest = new JSONObject();
	private String statusMessage = "";
	
	public ModifyProfileRequest(){
		
	}
	
	public void setStatusMessage(String message){
		this.statusMessage = message;
	}
	
	public String toString(){
		buildMessage();
		return clientRequest.toString();
	}
	
	private void buildMessage(){
		try {
			clientRequest.put("type", "id");
			clientRequest.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			clientRequest.put("status", statusMessage);
		} catch (JSONException e) {
		}
	}
}
