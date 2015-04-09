package team.artyukh.project.messages.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ViewGroupInfoUpdate {
	private String groupName = "";
	private String groupPurpose = "";
	private String groupAddress = "";
	private String groupDate = "";
	
	public ViewGroupInfoUpdate(JSONObject update){
		try {
			groupName = update.getString("name");
			groupPurpose = update.getString("purpose");
			groupAddress = update.getString("address");
			groupDate = update.getString("dateCreated");
			
			long unixSeconds = Long.parseLong(groupDate);
			Date date = new Date(unixSeconds);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy \n hh:mm a", Locale.CANADA);
			sdf.setTimeZone(TimeZone.getDefault());
			groupDate = sdf.format(date);
			
		} catch (JSONException e) {
			Log.i("GROUP_VIEW_EX", e.toString());
		}
	}
	
	public String getName(){
		return groupName;
	}
	
	public String getPurpose(){
		return groupPurpose;
	}
	
	public String getAddress(){
		return groupAddress;
	}
	
	public String getDate(){
		return groupDate;
	}
}
