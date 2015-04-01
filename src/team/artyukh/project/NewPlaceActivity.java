package team.artyukh.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import team.artyukh.project.messages.client.ImageUploadRequest;
import team.artyukh.project.messages.client.NewMarkerRequest;
import team.artyukh.project.messages.client.SaveMarkerRequest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;

public class NewPlaceActivity extends BindingActivity implements OnMapReadyCallback {
	
	private GoogleMap map;
	private SupportMapFragment mainFrag;
	private boolean mapLoaded = false;
	private SlidingDrawer sDrawer;
	private EditText etAddress;
	private EditText etTitle;
	private EditText etDescription;
	private ImageView markerPic;
	private Button btnAction;
	private boolean sendRequest = false;
	private boolean editMode = false;
	private String markerId, markerDescr, markerTitle, markerAddress;
	private static int RESULT_IMAGE = 123;
	private byte[] imgBytes = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_place);
		
		mainFrag = new SupportMapFragment();
		mainFrag.getMapAsync(this);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentMap, mainFrag).commit();
		
		sDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawerMap);
		markerPic = (ImageView) findViewById(R.id.ivMarkerPicture);
        etAddress = (EditText) findViewById(R.id.etMarkerAddress);
		etTitle = (EditText) findViewById(R.id.etMarkerTitle);
		etDescription = (EditText) findViewById(R.id.etMarkerDescription);
		btnAction = (Button) findViewById(R.id.btnMarkerCreate);
		
		Bundle incoming = getIntent().getExtras();
				
		if(incoming != null){
			editMode = true;
			markerTitle = incoming.getString("title", "");
			markerDescr = incoming.getString("description", "");
			markerId = incoming.getString("id");
			markerAddress = incoming.getString("address");
			
			etTitle.setText(markerTitle);
			etDescription.setText(markerDescr);
			etAddress.setText(markerAddress);
			btnAction.setText("Apply");
			
			
			if(markerId != null){
				Bitmap bmp = getBitmap(getExternalFilesDir(Environment.DIRECTORY_PICTURES), markerId);
				if(bmp != null){
					markerPic.setImageBitmap(bmp);
//					ByteArrayOutputStream stream = new ByteArrayOutputStream();
//					bmp.compress(CompressFormat.JPEG, 70, stream);
//				    imgBytes = stream.toByteArray();
				}
			}
		}
	}
	
	public void checkAddress(View v) {
		new GeocodingTask(getBaseContext()).execute(etAddress.getText().toString());	
	}
	
	public void createMarker(View v){
		sendRequest = true;
		new GeocodingTask(getBaseContext()).execute(etAddress.getText().toString());
	}
	
	public void choosePicture(View v){
		Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_IMAGE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Bitmap picture = getBitmap(new File(picturePath));
			markerPic.setImageBitmap(picture);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    picture.compress(CompressFormat.JPEG, 70, stream);
		    imgBytes = stream.toByteArray();
		}
	}
	
	public void goBack(View v){
		finish();
	}
	
	@Override
	public void onMapReady(GoogleMap m) {
		
		map = m;
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//		map.setOnMapClickListener(this);
//		map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 2));
		mapLoaded = true;
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
				if(loc == null){
					etAddress.setError("Cannot locate this address");
					return;
				}
				sendRequest = false;
				
				String address = etAddress.getText().toString();
				String title = etTitle.getText().toString();
				String descr = etDescription.getText().toString();
				
				title = title.length() > 0 ? title : null;
				descr = descr.length() > 0 ? descr : null;
				
				if (!editMode) {
					NewMarkerRequest request = new NewMarkerRequest(title, descr, address, loc.latitude, loc.longitude);
					if (imgBytes != null) {
						request.addImage(Base64.encodeToString(imgBytes, Base64.NO_WRAP));
					}
					send(request.toString());
				} else {
					SaveMarkerRequest request = new SaveMarkerRequest(markerId);
					request.editInfo(title, descr, address, loc.latitude, loc.longitude);
					if (imgBytes != null) {
						request.addImage(Base64.encodeToString(imgBytes, Base64.NO_WRAP));
					}
					send(request.toString());
				}
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
		getMenuInflater().inflate(R.menu.new_place, menu);
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
