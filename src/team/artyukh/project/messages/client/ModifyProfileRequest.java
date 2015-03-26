package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;

public class ModifyProfileRequest {
	
	private JSONObject clientRequest = new JSONObject();
	private String statusMessage = null;
	private Boolean appearOffline = null;
	private Boolean muteSound = null;
	private Boolean blockMessages = null;
	private Boolean blockInvites = null;
	
	public ModifyProfileRequest(){
		
	}
	
	public void setStatusMessage(String message){
		this.statusMessage = message;
	}
	
	public void setAppearOffline(boolean flag){
		this.appearOffline = flag;
	}
	
	public void setMuteSound(boolean flag){
		this.muteSound = flag;
	}
	
	public void setBlockMessages(boolean flag){
		this.blockMessages = flag;
	}
	
	public void setBlockInvites(boolean flag){
		this.blockInvites = flag;
	}
	
	public String toString(){
		buildMessage();
		return clientRequest.toString();
	}
	
	private void buildMessage(){
		try {
			clientRequest.put("type", "id");
			clientRequest.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
			clientRequest.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			
			if(statusMessage != null){
				clientRequest.put("status", statusMessage);
			}
			
			if(appearOffline != null){
				clientRequest.put("appearOffline", appearOffline);
			}
			
			if(muteSound != null){
				clientRequest.put("muteSound", muteSound);
			}
			
			if(blockMessages != null){
				clientRequest.put("blockMessages", blockMessages);
			}
			
			if(blockInvites != null){
				clientRequest.put("blockInvites", blockInvites);
			}
		} catch (JSONException e) {
		}
	}
}
