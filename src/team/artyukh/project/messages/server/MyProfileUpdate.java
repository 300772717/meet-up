package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MyProfileUpdate {
	private String status;
	private String appearOffline;
	private String muteSound;
	private String blockMessages;
	private String blockInvites;
	
	public MyProfileUpdate(JSONObject update){
		try {
			status = update.getString("status");
			appearOffline = update.getString("appearOffline");
			muteSound = update.getString("muteSound");
			blockMessages = update.getString("blockMessages");
			blockInvites = update.getString("blockInvites");
		} catch (JSONException e) {
			Log.i("EX_MY_PROFILE", "ERR");
		}
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getAppearOffline(){
		return appearOffline;
	}
	
	public String getMuteSound(){
		return muteSound;
	}
	
	public String getBlockMessages(){
		return blockMessages;
	}
	
	public String getBlockInvites(){
		return blockInvites;
	}
}
