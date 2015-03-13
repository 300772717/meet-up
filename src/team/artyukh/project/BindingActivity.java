package team.artyukh.project;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.ConnectionService.ServiceBinder;
import team.artyukh.project.messages.client.InviteRequest;
import team.artyukh.project.messages.client.NewGroupRequest;
import team.artyukh.project.messages.client.UpdateGroupRequest;
import team.artyukh.project.messages.server.ChatUpdate;
import team.artyukh.project.messages.server.FriendIdUpdate;
import team.artyukh.project.messages.server.GroupUpdate;
import team.artyukh.project.messages.server.ImageDownloadUpdate;
import team.artyukh.project.messages.server.InviteUpdate;
import team.artyukh.project.messages.server.LoginUpdate;
import team.artyukh.project.messages.server.MapUpdate;
import team.artyukh.project.messages.server.PersonalMessageUpdate;
import team.artyukh.project.messages.server.RegisterUpdate;
import team.artyukh.project.messages.server.SearchUpdate;
import team.artyukh.project.messages.server.ViewCategoriesUpdate;
import team.artyukh.project.messages.server.ViewFriendCategoryUpdate;
import team.artyukh.project.messages.server.ViewFriendsUpdate;
import team.artyukh.project.messages.server.ViewMarkersUpdate;
import team.artyukh.project.messages.server.ViewProfileUpdate;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class BindingActivity extends FragmentActivity {
	
	private static final String PREFS_FILE = "team.artyukh.project.PREFS_FILE";
	private static SharedPreferences data;
	public static final String PREF_USER_ID = "USER_ID";
	public static final String PREF_LAT = "PREF_LAT";
	public static final String PREF_LON = "PREF_LON";
	public static final String PREF_FILT_FRIENDS = "PREF_FILT_FRIENDS";
	public static final String PREF_FILT_GROUP = "PREF_FILT_GROUP";
	public static final String PREF_FILT_NEARBY = "PREF_FILT_NEARBY";
	public static final String PREF_USERNAME = "USERNAME";
	public static final String PREF_PASSWORD = "PASSWORD";
	public static final String PREF_STATUS = "STATUS";
	public static final String PREF_APPEAR_OFFLINE = "PREF_APPEAR_OFFLINE";
	public static final String PREF_GROUP = "GROUP";
	public static final String PREF_GROUP_MEMBERS = "GROUP_MEMBERS";
	public static final String PREF_CHAT = "CHAT";
	public static final String PREF_FRIENDS = "FRIENDS";
	static final String PREF_PHONE_NUMBER = "PHONE_NUMBER";
	private BroadcastReceiver receiver;
	private ConnectionService connService;
    private boolean serviceBound = false;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = this.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String message = intent.getStringExtra("message");
				processMessage(message);
			}
		};
		
//		addNotificationSpinner();
    }
	
	private void addNotificationSpinner()
	{
	    ActionBar mActionBar = getActionBar();
	    mActionBar.setDisplayShowCustomEnabled( true );

	    LayoutInflater inflater = LayoutInflater.from( this );
	    View header = inflater.inflate( R.layout.notification_spinner, null );

//	    Spinner spnr = (Spinner) header.findViewById( R.id.spnrNotifications );
	    TextView spnr = (TextView) header.findViewById(R.id.tvNotify);
//	    tv.setText( textToSet );

	    Display display = getWindowManager().getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    int actionBarWidth = size.x;
//	    int actionBarWidth = DeviceHelper.getDeviceWidth( this ); //Google for this method. Kinda easy.

	    spnr.measure(0, 0);
	    int tvSize = spnr.getMeasuredWidth();
	    int leftSpace = 0;
	    try
	    {
	        View homeButton = findViewById( android.R.id.home );
	        final ViewGroup holder = (ViewGroup) homeButton.getParent();

	        View firstChild =  holder.getChildAt( 0 );
	        View secondChild =  holder.getChildAt( 1 );

	        leftSpace = firstChild.getWidth()+secondChild.getWidth();
	    }
	    catch ( Exception ignored )
	    {}

	    mActionBar.setCustomView( header );

	    if ( null != header )
	    {
	        ActionBar.LayoutParams params = (ActionBar.LayoutParams) header.getLayoutParams();

	        if ( null != params )
	        {
	            int leftMargin =  ( actionBarWidth / 2 - ( leftSpace ) ) - ( tvSize / 2 ) ;

	            params.leftMargin = 0 >= leftMargin ? 0 : leftMargin;
	        }
	    }
	}
	
	public static void setPref(String key, String value){
		data.edit().putString(key, value).apply();
		
	}
	
	public static void setPref(String key, double value){
		data.edit().putFloat(key, (float) value).apply();
	}
	
	public static String getStringPref(String key){
		return data.getString(key, "");
	}
	
	public static double getDoublePref(String key){
		return data.getFloat(key, 0);
	}
	
	public static void removePref(String key){
		data.edit().remove(key).apply();
	}
	
	protected void processMessage(String message){
		try {
			JSONObject msgObj = new JSONObject(message);
			String type = msgObj.getString("type");

			if (type.equals("req")) {
				applyUpdate(new MapUpdate(msgObj));
			} else if (type.equals("search")) {
				applyUpdate(new SearchUpdate(msgObj));
			} else if (type.equals("chat")) {
				applyUpdate(new ChatUpdate(msgObj));
			} else if (type.equals("login")) {
				applyUpdate(new LoginUpdate(msgObj));
			} else if (type.equals("register")) {
				applyUpdate(new RegisterUpdate(msgObj));
			} else if (type.equals("invite")) {
				applyUpdate(new InviteUpdate(msgObj));
			} else if (type.equals("newgroup")) {
				applyUpdate(new GroupUpdate(msgObj));
				Log.i("MEMBERS", msgObj.toString());
			} else if (type.equals("imagedownload")){
				applyUpdate(new ImageDownloadUpdate(msgObj));
			} else if (type.equals("viewprofile")){
				applyUpdate(new ViewProfileUpdate(msgObj));
			} else if(type.equals("friendidupdate")){
				applyUpdate(new FriendIdUpdate(msgObj));
			} else if(type.equals("viewfriends")){
				applyUpdate(new ViewFriendsUpdate(msgObj));
			} else if(type.equals("viewmarkers")){
				applyUpdate(new ViewMarkersUpdate(msgObj));
			} else if(type.equals("personalmessage")){
				applyUpdate(new PersonalMessageUpdate(msgObj));
			} else if(type.equals("viewfriendcategories")){
				applyUpdate(new ViewCategoriesUpdate(msgObj));
			} else if(type.equals("viewfriendcategory")){
				applyUpdate(new ViewFriendCategoryUpdate(msgObj));
			}

		} catch (JSONException e) {
			Log.i("EX_BIND", e.toString());
		}
	}
	
	protected void applyUpdate(LoginUpdate message){
		if(!message.getStatus()){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}
	
	protected void applyUpdate(ChatUpdate message) {

	}

	protected void applyUpdate(GroupUpdate message) {

	}

	protected void applyUpdate(RegisterUpdate message) {
		
	}

	protected void applyUpdate(MapUpdate message) {

	}
	
	protected void applyUpdate(ImageDownloadUpdate message){
		ListableAdapter.saveNewBitmap(message.getObjectId(), getBitmap(getExternalFilesDir(Environment.DIRECTORY_PICTURES), message.getObjectId()));
	}

	protected void applyUpdate(InviteUpdate message) {
		String host = "";
		final String groupId;
		
		groupId = message.getGroupId();
		host = message.getSender();
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("WARNING");
		b.setMessage(host + " has invited you to join their group.");
        b.setPositiveButton("Accept",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						joinGroup(groupId);
					}
				});
        b.setNegativeButton("Not now",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

        AlertDialog confirm = b.create();
        confirm.show();
	}
	
	protected void applyUpdate(PersonalMessageUpdate message){
		AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Message from: " + message.getSender());
		b.setMessage(message.getMessage());
        b.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

        AlertDialog confirm = b.create();
        confirm.show();
	}
	
	protected void applyUpdate(SearchUpdate message) {

	}
	
	protected void applyUpdate(ViewProfileUpdate message){
		
	}
	
	protected void applyUpdate(FriendIdUpdate message){
		
	}
	
	protected void applyUpdate(ViewFriendsUpdate message){
		
	}
	
	protected void applyUpdate(ViewMarkersUpdate message){
		
	}
	
	protected void applyUpdate(ViewCategoriesUpdate message){
		
	}
	
	protected void applyUpdate(ViewFriendCategoryUpdate message){
		
	}
	
	private void joinGroup(String groupId){
		send(new UpdateGroupRequest(groupId).toString());
	}
	
	public void send(String message){
		if(serviceBound){
			connService.send(message);
    	}
	}
	
	public void send(byte[] image){
		if(serviceBound){
			connService.send(image);
		}
	}
	
	protected void sendInvite(String username){
		InviteRequest invite = new InviteRequest(username);
		
		if(BindingActivity.getStringPref(BindingActivity.PREF_GROUP).equals("")){
			NewGroupRequest groupRequest = new NewGroupRequest();
			
			send(groupRequest.toString());
			connService.addPendingInvite(username);
			removePref(BindingActivity.PREF_CHAT);
			return;
		}
		send(invite.toString());
	}
	
	protected static Bitmap getBitmap(File dir, String objId){
//		File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		
		for(String filename : dir.list()){
			Log.i("CHECKING FILE", filename);
			if(filename.startsWith(objId)){
				File image = new File(dir, filename);
				
				final BitmapFactory.Options options = new BitmapFactory.Options();
			    options.inJustDecodeBounds = true;
			    BitmapFactory.decodeFile(image.getAbsolutePath(), options);
			    options.inSampleSize = calculateInSampleSize(options, 150, 150);
			    
			    options.inJustDecodeBounds = false;
				Bitmap bmp = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
//				Bitmap bmp = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
				return bmp;
			}
		}		
		return null;
	}
	
	protected static Bitmap getBitmap(File imageFile){		
		if(!imageFile.exists()) return null;
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
	    options.inSampleSize = BindingActivity.calculateInSampleSize(options, 150, 150);
	    
	    options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		
		return bmp;
	}
	
	protected static Bitmap getBitmap(byte[] data){		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(data, 0, data.length, options);
	    options.inSampleSize = BindingActivity.calculateInSampleSize(options, 150, 150);	    
	    options.inJustDecodeBounds = false;
	    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
	    
	    return bmp;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	Intent intent = new Intent(this, ConnectionService.class);
    	startService(intent);
    	bindService(intent, sConnection, Context.BIND_AUTO_CREATE);
    	LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ConnectionService.INTENT_MESSAGE));
    }
    
    @Override
    protected void onStop() {
        if(serviceBound){
        	unbindService(sConnection);
        	serviceBound = false;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
    
    protected void onServiceConnected(){
    	//OVERRIDE THIS IN ACTIVITIES THAT NEED TO HANDLE ONSERVICECONNECTED
    }
    
    private ServiceConnection sConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ServiceBinder binder = (ServiceBinder) service;
			connService = binder.getService();
			serviceBound = true;
			BindingActivity.this.onServiceConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
		}
    	
    };
}
