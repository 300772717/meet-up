package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RegisterUpdate {
	private boolean status;
	
	public RegisterUpdate(JSONObject regUpdate){
		try {
			if(regUpdate.getString("status").equals("success")){
				this.status = true;
			}
			else{
				this.status = false;
			}
		} catch (JSONException e) {
		}
		
		Log.i("REG", status + "");
	}
	
	public boolean getStatus(){
		return status;
	}
}
