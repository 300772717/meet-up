package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ChatUpdate {
	private String message;
	private String from;
	private String id;
	private String picDate;
	
	public ChatUpdate(JSONObject chatUpdate){
		try {
			this.message = chatUpdate.getString("text");
			this.from = chatUpdate.getString("username");
			this.id = chatUpdate.getString("id");
			this.picDate = chatUpdate.getString("picDate");
		} catch (JSONException e) {
			Log.i("EX CHAT", e.toString());
		}
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getSender(){
		return from;
	}
	
	public String getId(){
		return id;
	}
	
	public String getImageDate(){
		return picDate;
	}
}
