package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginUpdate {
	private boolean status;
	private String id = "";
	
	public LoginUpdate(JSONObject loginUpdate){
		try {
			if(loginUpdate.getString("status").equals("success")){
				this.status = true;
				this.id = loginUpdate.getString("id");
			}
			else{
				this.status = false;
			}
		} catch (JSONException e) {
		}
	}
	
	public boolean getStatus(){
		return status;
	}

	public String getUserId() {
		return id;
	}
	
}
