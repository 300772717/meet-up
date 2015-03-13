package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MyProfileUpdate {
	private String status;
	private String appearOffline;
	
	public MyProfileUpdate(JSONObject update){
		try {
			status = update.getString("status");
			appearOffline = update.getString("appearOffline");
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
}
