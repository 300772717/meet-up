package team.artyukh.project.messages.client;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageDownloadRequest {
	
	JSONObject request = new JSONObject();
	
	public ImageDownloadRequest(String objectId){
		try {
			request.put("type", "imagedownload");
			request.put("id", objectId);
		} catch (JSONException e) {
		}	
	}
	
	public String toString(){
		return request.toString();
	}
}
