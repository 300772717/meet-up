package team.artyukh.project.messages.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class ImageDownloadUpdate {
	private String objId;
	private String picDate;
	private byte[] image;
	
	public ImageDownloadUpdate(JSONObject imageUpdate){
		
		try {
			this.objId = imageUpdate.getString("objId");
			this.picDate = imageUpdate.getString("picDate");
			this.image = Base64.decode(imageUpdate.getString("image"), Base64.NO_WRAP);
		} catch (JSONException e) {
			this.objId = null;
			this.picDate = null;
			image = null;
		}
	}
	
	public String getImageName(){
		return objId + "-" + picDate;
	}
	
	public String getObjectId(){
		return objId;
	}
	
	public String getImageDate(){
		return picDate;
	}
	
	public byte[] getImageBytes(){
		return image;
	}
}
