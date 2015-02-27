package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewProfileUpdate {
	private String username;
	private String status;
	private String userId;
	private String picDate;
	
	public ViewProfileUpdate(JSONObject update){
		try {
			username = update.getString("username");
			status = update.getString("status");
			userId = update.getString("id");
			picDate = update.getString("picDate");
		} catch (JSONException e) {
		}
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public String getImageDate(){
		return picDate;
	}
}
