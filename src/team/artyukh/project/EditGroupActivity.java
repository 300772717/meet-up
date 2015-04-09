package team.artyukh.project;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import team.artyukh.project.messages.client.EditGroupRequest;
import team.artyukh.project.messages.client.ViewGroupInfoRequest;
import team.artyukh.project.messages.server.ViewGroupInfoUpdate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class EditGroupActivity extends BindingActivity implements OnMapReadyCallback {
	private SlidingDrawer sDrawer;
	private GoogleMap map;
	private SupportMapFragment mainFrag;
	private EditText etAddress;
	private EditText etGroupName;
	private EditText etPurpose;
	private TextView tvDate;
	private Button btnAction;
	private boolean mapLoaded = false;
	private boolean sendRequest = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		
		sDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawerGroupMap);
		etAddress = (EditText) findViewById(R.id.etGroupAddress);
		etGroupName = (EditText) findViewById(R.id.etGroupName);
		etPurpose = (EditText) findViewById(R.id.etGroupPurpose);
		tvDate = (TextView) findViewById(R.id.tvGroupDate);
		btnAction = (Button) findViewById(R.id.btnGroupApply);
		
		mainFrag = new SupportMapFragment();
		mainFrag.getMapAsync(this);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentGroupMap, mainFrag).commit();
	}
	
	@Override
	protected void onServiceConnected(){
		send(new ViewGroupInfoRequest().toString());
	}
	
	@Override
	protected void applyUpdate(ViewGroupInfoUpdate message){
		etGroupName.setText(message.getName());
		etPurpose.setText(message.getPurpose());
		etAddress.setText(message.getAddress());
		tvDate.setText(message.getDate());
	}
	
	@Override
	public void onMapReady(GoogleMap m) {
		map = m;
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mapLoaded = true;
	}
	
	public void checkAddress(View v) {
		new GeocodingTask(getBaseContext()).execute(etAddress.getText().toString());	
	}
	
	public void editGroup(View v){
		sendRequest = true;
		new GeocodingTask(getBaseContext()).execute(etAddress.getText().toString());
	}
	
	public void goBack(View v){
		finish();
	}
	
	private class GeocodingTask extends AsyncTask<String, Void, LatLng>{
		Context mContext;
		
		public GeocodingTask(Context context){
			super();
			mContext = context;
		}

		@Override
		protected LatLng doInBackground(String... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			
			List<Address> addr = null;
			LatLng loc = null;
			
			try {
				addr = geocoder.getFromLocationName(params[0], 1);
			} catch (IOException e) {
			}
			
			if(addr != null && addr.size() > 0 ){
				Address address = addr.get(0);
				
				loc = new LatLng(address.getLatitude(), address.getLongitude());
				
//				addressText = String.format("%s, %s, %s",
//	                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
//	                    address.getLocality(),	                    
//	                    address.getCountryName());				
			}
			
			return loc;
		}		
		
		@Override
		protected void onPostExecute(LatLng loc) {
			
			if(sendRequest){
				sendRequest = false;
				if(loc == null){
					etAddress.setError("Cannot locate this address");
					return;
				}
				
				
				String address = etAddress.getText().toString();
				String name = etGroupName.getText().toString();
				String purpose = etPurpose.getText().toString();
				
				name = name.length() > 0 ? name : null;
				purpose = purpose.length() > 0 ? purpose : null;
				
				send(new EditGroupRequest(name, purpose, address).toString());
				finish();
			}
			else{
				if(mapLoaded){
					if(loc == null){
						etAddress.setError("Cannot locate this address");
						return;
					}
					sDrawer.open();
					map.clear();
					map.addMarker(new MarkerOptions().position(loc));
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
				}
				
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
