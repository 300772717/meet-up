package team.artyukh.project;

import team.artyukh.project.messages.client.ImageDownloadRequest;
import team.artyukh.project.messages.client.ViewProfileRequest;
import team.artyukh.project.messages.server.ImageDownloadUpdate;
import team.artyukh.project.messages.server.ViewProfileUpdate;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherProfileActivity extends BindingActivity {

	private TextView username;
	private TextView status;
	private ImageView profilePic;
	private String userId;
	private String userName = null;
	private boolean needImage = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_profile);
		
		Intent intent = getIntent();
		userId = intent.getStringExtra("userid");
		
		username = (TextView) findViewById(R.id.tvOtherUsername);
		status = (TextView) findViewById(R.id.tvOtherStatus);
		profilePic = (ImageView) findViewById(R.id.ivUserPicture);
		
		Bitmap image = getBitmap(getExternalFilesDir(Environment.DIRECTORY_PICTURES), userId);
		if(image == null){
			needImage = true;
		}
		else{
			profilePic.setImageBitmap(image);
			needImage = false;
		}
	}
	
	@Override
	protected void onServiceConnected(){
		send((new ViewProfileRequest(userId)).toString());
		if(needImage){
			send((new ImageDownloadRequest(userId)).toString());
		}
	}
	
	@Override
	protected void applyUpdate(ViewProfileUpdate update){
		userName = update.getUsername();
		username.setText(update.getUsername());
		status.setText(update.getStatus());	
	}
	
	@Override
	protected void applyUpdate(ImageDownloadUpdate update){
		if(!update.getObjectId().equals(userId)) return;
		
		Bitmap image = getBitmap(getExternalFilesDir(Environment.DIRECTORY_PICTURES),update.getObjectId());
		if(image != null){
			profilePic.setImageBitmap(image);
		}
	}
	
	public void sendInvite(View v){
		if(userName != null){
			sendInvite(userName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.other_profile, menu);
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
