package team.artyukh.project.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.R;
import team.artyukh.project.messages.client.ImageDownloadRequest;
import team.artyukh.project.messages.client.MapRequest;
import team.artyukh.project.messages.client.MyPositionRequest;
import team.artyukh.project.messages.client.SearchRequest;
import team.artyukh.project.messages.client.SetMarkerRequest;
import team.artyukh.project.messages.server.MapObjectMarker;
import team.artyukh.project.messages.server.MapObject;
import team.artyukh.project.messages.server.MapUpdate;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class MapViewFragment extends Fragment implements OnMapClickListener, OnMapReadyCallback, OnMapLongClickListener {
	
	private GoogleMap map;
	private SupportMapFragment mainFrag;
	private boolean mapLoaded = false;
	private double minLat, minLon, maxLat, maxLon;
	private CheckBox chkFriends, chkGroup, chkNearby;
	private ImageButton btnLocateUser;
	private LatLng myLoc;
	private final double EARTH_RAD = 6371;
	private final double BOX_SIZE = 10;
	private ArrayList<MapObject> people = new ArrayList<MapObject>();
	private ArrayList<MapObjectMarker> markersInfo = new ArrayList<MapObjectMarker>();
	private ArrayList<Circle> circles = new ArrayList<Circle>();
	private BindingActivity parent;
	private Thread requestThread;
	
	private void setUp(){
		parent = (BindingActivity) getActivity();
		mainFrag = new SupportMapFragment();
		mainFrag.getMapAsync(this);

		Double myLat, myLon;
		myLat = BindingActivity.getDoublePref(BindingActivity.PREF_LAT);
		myLon = BindingActivity.getDoublePref(BindingActivity.PREF_LON);

		myLoc = new LatLng(myLat, myLon);
		calculateBox(myLoc);

		requestThread = new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(5 * 1000);
						requestUpdate();
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		
		requestThread.start();
	}
	
	@Override
	public void onMapClick(LatLng loc) {
		//Hide all info windows on map click
		//Delete marker if old and info window not showing
		for(MapObjectMarker m : markersInfo){
			m.setSnippet("false");
			m.hideInfoWindow();
			if (m.isOld() && !m.isVisible()) {
				m.removeMarker();
			}
			m.setIcon(BitmapDescriptorFactory.defaultMarker());
		}
		myLoc = loc;
		BindingActivity.setPref(BindingActivity.PREF_LAT, loc.latitude);
		BindingActivity.setPref(BindingActivity.PREF_LON, loc.longitude);
		calculateBox(myLoc);
		updateMyLocation();	
	}
	
	public void userMoved(JSONObject loc){
		double lat, lon;
		try {
			lat = loc.getDouble("lat");
			lon = loc.getDouble("lon");
			
			myLoc = new LatLng(lat, lon);
			calculateBox(myLoc);
			updateMyLocation();
		} catch (JSONException e) {
		}
		
	}
	
	@Override
	public void onMapLongClick(final LatLng loc) {
    	boolean inRange = false;
		float[] dist = new float[3];
    	ArrayList<MapObject> query = new ArrayList<MapObject>();
    	
		for (MapObject obj : people) {
			Location.distanceBetween(obj.getLocation().latitude, obj.getLocation().longitude, loc.latitude, loc.longitude, dist);
			if (dist[0] < 1000) {
				query.add(obj);
			}
		}
		if(query.size() > 0) inRange = true;
		
		final ArrayList<MapObject> query2 = query;
		AlertDialog.Builder b = new AlertDialog.Builder(parent);
        b.setTitle("WARNING");
		b.setMessage("");
		b.setNeutralButton("Place Marker",
				new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int id) {
						parent.send(new SetMarkerRequest(loc.latitude, loc.longitude).toString());
						
					}
				});
        b.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
        if(inRange){
        	b.setPositiveButton("Who is here?",
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						parent.send(new SearchRequest(query2).toString());
    					}
    				});
        }

        AlertDialog confirm = b.create();
        confirm.show();
		
		
	}
	
	private void updateMyLocation() {
		parent.send(new MyPositionRequest(myLoc.longitude, myLoc.latitude).toString());
	}
	
	public void updateMap(MapUpdate message){
		if(!mapLoaded) return;
		Circle crc;
		Marker mrk;
		String userId = BindingActivity.getStringPref(BindingActivity.PREF_USER_ID);
		
		for(Circle c : circles){
			c.remove();
		}
		
		for(Iterator<MapObjectMarker> itr = markersInfo.iterator(); itr.hasNext();){
			MapObjectMarker m = itr.next();
			
			if(!m.isVisible()){
				m.removeMarker();
				itr.remove();
			}
			else {
//				m.removeMarker();
				m.setOld();
			}
		}
//		map.clear();
		circles.clear();
		
    	people = message.getPeople();
    	int fillColor, strokeColor;
    	for(MapObject obj : people){
    		if(obj.getId().equals(userId)){
    			fillColor = Color.GREEN;
    			strokeColor = Color.GREEN;
    		}
    		else {
    			fillColor = Color.RED;
    			strokeColor = Color.BLACK;
    		}
    		float diff = 12 - map.getCameraPosition().zoom;
			float rad = (float) (100 * Math.pow(2, diff));
			
    		LatLng spot = obj.getLocation();
    		crc = map.addCircle(new CircleOptions()
    		.center(spot)
    		.radius(rad)
    		.fillColor(fillColor)
    		.strokeColor(strokeColor));
			circles.add(crc);
    	}
    	
    	markersInfo.addAll(message.getMarkers());
    	int i = 0;
    	MapObjectMarker temp = null;
    	for(MapObjectMarker obj : markersInfo){
    		if(obj.isOld()){
    			obj.setTitle(String.valueOf(i));
    			temp = obj;
    			i++;
    			continue;
    		}
    		LatLng spot = obj.getLocation();
    		MarkerOptions opt = new MarkerOptions();
    		opt.title(String.valueOf(i));
    		opt.position(spot);
    		opt.snippet("false");
//    		if(obj.isOld()){
//    			opt.snippet("true");
//    		}
//    		else{
//    			opt.snippet("false");
//    		}
    		mrk = map.addMarker(opt);
    		//TODO: Rewrite this to store marker id + associated object in a hashmap
    		//YOU IDIOT
    		obj.addMarker(mrk);
//    		if(obj.isOld()){
//    			temp = obj;
//    		}
    		i++;
    	}
    	
    	if(temp != null){
    		temp.showInfoWindow();
    	}
	}
	
	private OnMarkerClickListener markerListener = new OnMarkerClickListener(){
		@Override
		public boolean onMarkerClick(Marker marker) {		
			return false;
		}
		
	};
	
	private InfoWindowAdapter iwAdapter = new InfoWindowAdapter() {

		String desc;
    	String markerId;
    	String picDate;
    	Bitmap bmp;
    	View v;
    	ImageView ivIcon;
        TextView tvTitle;
        TextView tvDescr;
		
        @Override
        public View getInfoWindow(Marker marker) {
        	return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
        	int index = Integer.parseInt(marker.getTitle());
        	v = parent.getLayoutInflater().inflate(R.layout.info_window_marker, null);
        	ivIcon = (ImageView) v.findViewById(R.id.ivInfoIcon);
            tvTitle = (TextView) v.findViewById(R.id.tvInfoTitle);
            tvDescr = (TextView) v.findViewById(R.id.tvInfoDescr);
        	
            for(MapObjectMarker m : markersInfo){
				m.setSnippet("false");
				m.setIcon(BitmapDescriptorFactory.defaultMarker());
            }
            
            marker.setSnippet("true");
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            makeView(markersInfo.get(index));
            
            for (MapObjectMarker m : markersInfo) {
				if (m.isOld() && !m.isVisible()) {
					m.hideInfoWindow();
					m.removeMarker();
				}
			}
            
            return v;
        }
        
        private void makeView(MapObjectMarker m){
        	
        	markerId = m.getId();
        	picDate = m.getImageDate();
            desc = m.getDescription();  
            
            bmp = BindingActivity.getBitmap(parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES), markerId, picDate);
            if(bmp != null){
            	ivIcon.setImageBitmap(bmp);
            }
            else{
            	parent.send(new ImageDownloadRequest(markerId).toString());
            }
            
            tvDescr.setText(desc);
        }
    };
    
    private OnCameraChangeListener cameraListener = new OnCameraChangeListener(){
		@Override
		public void onCameraChange(CameraPosition pos) {
			float diff = 12 - pos.zoom;
			float rad = (float) (100 * Math.pow(2, diff));
			for(Circle c : circles){
				c.setRadius(rad);
			}
		}
    };
	
	private void requestUpdate() {
		//THE REQUEST CONSTRUCTOR TAKES RADIAN ANGLES
		boolean getFriends, getGroup, getNearby;
		getFriends = chkFriends.isChecked();
		getGroup = chkGroup.isChecked();
		getNearby = chkNearby.isChecked();
		
		parent.send(new MapRequest(getFriends, getGroup, getNearby, minLat, minLon, maxLat, maxLon).toString());
	}
	
	public void checkClick(View v){
		
	}
	
	@Override
	public void onMapReady(GoogleMap m) {
		
		map = m;
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);
		map.setOnMarkerClickListener(markerListener);
		map.setInfoWindowAdapter(iwAdapter);
		map.setOnCameraChangeListener(cameraListener);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 12));
		mapLoaded = true;
	}
	
	private void calculateBox(LatLng loc) {
		double radDist = BOX_SIZE / EARTH_RAD;
		double radLat, radLon;
		final double MIN_LAT = -Math.PI / 2;
		final double MAX_LAT = Math.PI / 2;
		final double MIN_LON = -Math.PI;
		final double MAX_LON = Math.PI;
		radLat = Math.toRadians(loc.latitude);
		radLon = Math.toRadians(loc.longitude);

		minLat = radLat - radDist;
		maxLat = radLat + radDist;

		if (minLat > MIN_LAT && maxLat < MAX_LAT) {
			double deltaLon = Math.asin(Math.sin(radDist) / Math.cos(radLat));
			minLon = radLon - deltaLon;
			if (minLon < MIN_LON)
				minLon += 2d * Math.PI;
			maxLon = radLon + deltaLon;
			if (maxLon > MAX_LON)
				maxLon -= 2d * Math.PI;
		} else {
			minLat = Math.max(minLat, MIN_LAT);
			maxLat = Math.min(maxLat, MAX_LAT);
			minLon = MIN_LON;
			maxLon = MAX_LON;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		setUp();
		
    	FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_map, mainFrag).commit();
        
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        		
		chkFriends = (CheckBox) root.findViewById(R.id.cbFriends);
		chkGroup = (CheckBox) root.findViewById(R.id.cbGroup);
		chkNearby = (CheckBox) root.findViewById(R.id.cbNearby);
		btnLocateUser = (ImageButton) root.findViewById(R.id.btnLocateUser);
		
		chkFriends.setChecked(getCheckedPref(BindingActivity.PREF_FILT_FRIENDS));
		chkGroup.setChecked(getCheckedPref(BindingActivity.PREF_FILT_GROUP));
		chkNearby.setChecked(getCheckedPref(BindingActivity.PREF_FILT_NEARBY));
		
		chkFriends.setOnClickListener(checkBoxListener);
		chkGroup.setOnClickListener(checkBoxListener);
		chkNearby.setOnClickListener(checkBoxListener);
		btnLocateUser.setOnClickListener(locateUserListener);
		

        return root;
	}
	
	private boolean getCheckedPref(String key){
		String checked = BindingActivity.getStringPref(key);	
		return Boolean.parseBoolean(checked);
	}
	
	OnClickListener checkBoxListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			CheckBox cb = (CheckBox) v;
			String status = String.valueOf(cb.isChecked());
			switch(v.getId()){
			case R.id.cbFriends:
				BindingActivity.setPref(BindingActivity.PREF_FILT_FRIENDS, status);
				break;
			case R.id.cbGroup:
				BindingActivity.setPref(BindingActivity.PREF_FILT_GROUP, status);
				break;
			case R.id.cbNearby:
				BindingActivity.setPref(BindingActivity.PREF_FILT_NEARBY, status);
				break;
			}
		}
	};
	
	OnClickListener locateUserListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			float zoom = map.getCameraPosition().zoom;
			if(zoom < 12){
				zoom = 12;
			}
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, zoom));		
		}
	};
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		requestThread.interrupt();
	}
	
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public MapViewFragment() {
	}

}
