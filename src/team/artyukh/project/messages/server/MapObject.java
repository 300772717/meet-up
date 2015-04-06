package team.artyukh.project.messages.server;

import com.google.android.gms.maps.model.LatLng;

public class MapObject {
	public static final int TYPE_PERSON = 0;
	public static final int TYPE_MARKER = 1;
	
	private LatLng location;
	private String id;
	private int type;
	
	public MapObject(int objType, double lat, double lon, String objId){
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
	
	public int getType(){
		return type;
	}
}
