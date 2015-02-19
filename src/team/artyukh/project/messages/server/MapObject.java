package team.artyukh.project.messages.server;

import com.google.android.gms.maps.model.LatLng;

public class MapObject {
	private LatLng location;
	private String id;
	private String type;
	
	public MapObject(String objType, double lat, double lon, String objId){
		this.location = new LatLng(lat, lon);
		this.id = objId;
		this.type = objType;
	}
	
	public MapObject(LatLng loc, String objId){
		this.location = loc;
		this.id = objId;
	}
	
	public LatLng getLocation(){
		return location;
	}
	
	public String getId(){
		return id;
	}
	
	public String getType(){
		return type;
	}
}
