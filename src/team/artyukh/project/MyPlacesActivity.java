package team.artyukh.project;

import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.messages.client.RemoveMarkerRequest;
import team.artyukh.project.messages.client.SaveMarkerRequest;
import team.artyukh.project.messages.client.ViewMarkersRequest;
import team.artyukh.project.messages.server.ViewMarkersUpdate;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyPlacesActivity extends BindingActivity {

	private TextView noPlaces;
	private Button btnCreate;
	private Button btnSave;
	private Button btnRemove;
	private ListableFragment mainFrag;
	private boolean showingPlaces = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_places);		
		
		noPlaces = (TextView) findViewById(R.id.tvNoPlaces);
		btnCreate = (Button) findViewById(R.id.btnNewMarker);
		btnSave = (Button) findViewById(R.id.btnSaveMarker);
		btnRemove = (Button) findViewById(R.id.btnRemoveMarker);
		
		mainFrag = new ListableFragment(MyPlacesActivity.this);
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_places, mainFrag).commit();
        mainFrag.makeSelectable();
        
        getSupportFragmentManager().beginTransaction().hide(mainFrag).commit();
        noPlaces.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
        btnRemove.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void onServiceConnected(){
		send(new ViewMarkersRequest().toString());
	}
	
	@Override
	protected void applyUpdate(ViewMarkersUpdate message){
		if(message.getMarkers().size() > 0){
			mainFrag.setAdapter(new ListableAdapter(MyPlacesActivity.this, message.getMarkers(), true));
			getSupportFragmentManager().beginTransaction().show(mainFrag).commit();
	        noPlaces.setVisibility(View.INVISIBLE);
	        btnSave.setVisibility(View.VISIBLE);
	        btnRemove.setVisibility(View.VISIBLE);
		}
		else{
			getSupportFragmentManager().beginTransaction().hide(mainFrag).commit();
	        noPlaces.setVisibility(View.VISIBLE);
	        btnSave.setVisibility(View.INVISIBLE);
	        btnRemove.setVisibility(View.INVISIBLE);
		}
	}
	
	public void createMarker(View v){
		Intent intent = new Intent(MyPlacesActivity.this, NewPlaceActivity.class);
		startActivity(intent);
	}
	
	public void saveMarker(View v){
		//ADD CONDITIONAL BASED ON ILISTABLE GET STATUS
		int position = mainFrag.getSelectedItemPosition();
		if(position < 0) return;
		IListable marker = mainFrag.getAdapter().getItem(position);
		send(new SaveMarkerRequest(marker.getId()).toString());
	}
	
	public void removeMarker(View v){
		int position = mainFrag.getSelectedItemPosition();
		if(position < 0) return;
		IListable marker = mainFrag.getAdapter().getItem(position);
		send(new RemoveMarkerRequest(marker.getId()).toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_places, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    case R.id.action_settings:
	    	return true;
	    }
		return super.onOptionsItemSelected(item);
	}
}
