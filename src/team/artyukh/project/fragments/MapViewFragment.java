package team.artyukh.project.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.R;
import team.artyukh.project.messages.client.MapRequest;
import team.artyukh.project.messages.client.MyPositionRequest;
import team.artyukh.project.messages.client.SearchRequest;
import team.artyukh.project.messages.client.SetMarkerRequest;
import team.artyukh.project.messages.server.MapObject;
import team.artyukh.project.messages.server.MapUpdate;
import android.location.Location;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;


public class MapViewFragment extends Fragment implements OnMapClickListener, OnMapReadyCallback, OnMapLongClickListener {
	
	private GoogleMap map;
	private boolean mapLoaded = false;
	private double minLat, minLon, maxLat, maxLon;
	private CheckBox chkFriends, chkGroup, chkNearby;
	private LatLng myLoc;
	private String phone;
	private final double EARTH_RAD = 6371;
	private final double BOX_SIZE = 10;
	private ArrayList<MapObject> people = new ArrayList<MapObject>();
	private ArrayList<MapObject> markers = new ArrayList<MapObject>();
	private BindingActivity parent;
	private SupportMapFragment mainFrag;
	private Thread requestThread;
	
	private void setUp(){
		parent = (BindingActivity) getActivity();
		mainFrag = new SupportMapFragment();
		mainFrag.getMapAsync(this);
		
		TelephonyManager tmgr = (TelephonyManager) parent.getSystemService(Context.TELEPHONY_SERVICE);

		phone = tmgr.getLine1Number();

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
		myLoc = loc;
		BindingActivity.setPref(BindingActivity.PREF_LAT, loc.latitude);
		BindingActivity.setPref(BindingActivity.PREF_LON, loc.longitude);
		calculateBox(myLoc);
		updateMyLocation();	
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
		
		map.clear();
    	people = message.getPeople();
    	for(MapObject obj : people){
    		LatLng spot = obj.getLocation();
			map.addCircle(new CircleOptions()
    		.center(spot)
    		.radius(100)
    		.fillColor(Color.RED)
    		.strokeColor(Color.BLACK));;
    	}
    	
    	markers = message.getMarkers();
    	for(MapObject obj : markers){
    		LatLng spot = obj.getLocation();
    		map.addMarker(new MarkerOptions()
    		.position(spot));
    	}
	}
	
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
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 2));
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
		
		chkFriends.setChecked(getCheckedPref(BindingActivity.PREF_FILT_FRIENDS));
		chkGroup.setChecked(getCheckedPref(BindingActivity.PREF_FILT_GROUP));
		chkNearby.setChecked(getCheckedPref(BindingActivity.PREF_FILT_NEARBY));
		
		chkFriends.setOnClickListener(checkBoxListener);
		chkGroup.setOnClickListener(checkBoxListener);
		chkNearby.setOnClickListener(checkBoxListener);

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
			Log.i("CHECKED", status);
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
