package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

public class InviteUpdate {
	private String groupId;
	private String from;
	
	public InviteUpdate(JSONObject inviteUpdate){
		try {
			this.groupId = inviteUpdate.getString("group");
			this.from = inviteUpdate.getString("username");
		} catch (JSONException e) {
		}
	}
	
	public String getGroupId(){
		return groupId;
	}
	
	public String getSender(){
		return from;
	}
	
}
