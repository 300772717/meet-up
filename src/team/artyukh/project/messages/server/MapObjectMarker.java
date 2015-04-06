package team.artyukh.project.messages.server;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;

public class MapObjectMarker extends MapObject {
	private String userId;
	private String descr;
	private String picDate;
	private Marker marker = null;
	private boolean haveMarker = false;
	private boolean old;
	
	public MapObjectMarker(int objType, double lat, double lon, String objId, String userId, String descr, String picDate){
		super(objType, lat, lon, objId);
		
		this.userId = userId;
		this.descr = descr;
		this.picDate = picDate;
		this.old = false;
	}
	
	public void addMarker(Marker mrk){
		if(haveMarker) marker.remove();
		this.marker = mrk;
//		this.old = false;
		this.haveMarker = true;
	}
	
	public void removeMarker(){
		if(!haveMarker) return;
		haveMarker = false;
		marker.remove();
	}
	
	public void showInfoWindow(){
		if(!haveMarker) return;
		marker.showInfoWindow();
	}
	
	public void hideInfoWindow(){
		if(!haveMarker || !marker.isVisible()) return;
		marker.hideInfoWindow();
	}
	
	public void setIcon(BitmapDescriptor icon){
		if(!haveMarker) return;
		marker.setIcon(icon);
	}
	
	public void setTitle(String title){
		if(!haveMarker) return;
		marker.setTitle(title);
	}
	
	public void setSnippet(String snip){
		if(!haveMarker) return;
		marker.setSnippet(snip);
	}
	
	public void setOld(){
		this.old = true;
	}
	
	public boolean isOld(){
		return old;
	}
	
	public boolean isVisible(){
		if(!haveMarker) return false;
		boolean vis = Boolean.parseBoolean(marker.getSnippet());
		return vis;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public String getImageDate(){
		return picDate;
	}
	
	public String getDescription(){
		return descr;
	}
}
