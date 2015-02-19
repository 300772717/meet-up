package team.artyukh.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.lists.IListable;
import team.artyukh.project.messages.client.ImageDownloadRequest;
import team.artyukh.project.messages.server.ChatUpdate;
import team.artyukh.project.messages.server.GroupUpdate;
import team.artyukh.project.messages.server.ImageDownloadUpdate;
import team.artyukh.project.messages.server.SearchUpdate;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ConnectionService extends Service {
	//COMMENT CHANGE AGAIN AND AGAIN AND A THIRD TIME
	public static final String INTENT_MESSAGE = "INTENT_MESSAGE";
	private static final String PREFS_FILE = "team.artyukh.project.PREFS_FILE";
	private SharedPreferences data;
	private ArrayList<String> pendingInvites = new ArrayList<String>();
	private final String SERVER_IP = "ws://192.168.123.100:2222";
	private final IBinder mBinder = new ServiceBinder();
	private MySocketClient msc;
	
	public class ServiceBinder extends Binder {
		ConnectionService getService(){
			return ConnectionService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate(){
		data = this.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
		URI servIp = URI.create(SERVER_IP);
		msc = new MySocketClient(servIp);
		msc.connect();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		
		return Service.START_STICKY;
	}
	
	public void send(String message){
		msc.send(message);
	}
	
	public void send(byte[] image){
		msc.send(image);
	}
	
	private void updateChat(ChatUpdate message){
		
		try {
			JSONArray msgs;
			JSONObject msg = new JSONObject();
			msg.put("text", message.getMessage());
			msg.put("sender", message.getSender());
			msg.put("id", message.getId());
			msg.put("picDate", message.getImageDate());
			
			checkImageFile(message.getId(), message.getImageDate());
			
			msgs = new JSONArray(data.getString(BindingActivity.PREF_CHAT, "[]"));
			msgs.put(msg);
			data.edit().putString(BindingActivity.PREF_CHAT, msgs.toString()).apply();
		} catch (JSONException e) {
		}
	}
	
	private void checkSearchImages(SearchUpdate message){
		for(IListable obj : message.getResult()){
			checkImageFile(obj.getId(), obj.getImageDate());
		}
	}
	
	private void updateGroupMembers(GroupUpdate message){
		JSONArray members = new JSONArray();
		for(IListable p : message.getMembers()){
			JSONObject member = new JSONObject();
			try {
				member.put("username", p.getTitle());
				member.put("status", p.getBody());
				member.put("id", p.getId());
				member.put("picDate", p.getImageDate());
				
				checkImageFile(p.getId(), p.getImageDate());
				
				members.put(members.length(), member);
				
			} catch (JSONException e) {
			}
		}
		
		data.edit().putString(BindingActivity.PREF_GROUP_MEMBERS, members.toString()).apply();
		
		if(!BindingActivity.getStringPref(BindingActivity.PREF_GROUP).equals(message.getGroupId())){
			BindingActivity.removePref(BindingActivity.PREF_CHAT);
			BindingActivity.setPref(BindingActivity.PREF_GROUP, message.getGroupId());
		}
		
		if(!BindingActivity.getStringPref(BindingActivity.PREF_GROUP).equals("")){
			for (String username : pendingInvites) {
				JSONObject invObj = new JSONObject();

				try {
					invObj.put("type", "invite");
					invObj.put("inviteuser", username);
					invObj.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
					invObj.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				send(invObj.toString());
			}
			pendingInvites.clear();
		}
	}
	
	private void checkImageFile(String objId, String picDate){
		File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), objId + "-" + picDate);
//		Log.i("CHECKING FILE", objId + "-" + picDate);
		if(!file.exists() && picDate.length() > 0){
			Log.i("REQUEST SENT", objId + "-" + picDate);
			send(new ImageDownloadRequest(objId).toString());
		}
	}
	
	private void saveImageFile(ImageDownloadUpdate update){
		File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		String[] files = dir.list();
		Log.i("REQUEST ANS", update.getImageName());
		for(String filename : files){
			if(filename.startsWith(update.getObjectId())){
				File oldImage = new File(dir, filename);
				oldImage.delete();
				break;
			}
		}
		
		File newImage = new File(dir, update.getImageName());
		
		try {
			newImage.createNewFile();
		} catch (IOException e) {
			Log.i("EX NEW FILE", e.toString());
			return;
		}
		
		try {
			
			OutputStream os = new FileOutputStream(newImage);
			byte[] data = update.getImageBytes();
//			InputStream is = new ByteArrayInputStream(data);
			final BitmapFactory.Options options = new BitmapFactory.Options();
//		    options.inSampleSize = 16;
//		    options.inJustDecodeBounds = true;
			Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length, options);
			Log.i("BOUNDS", "WIDE " + options.outWidth + " HIGH " + options.outHeight);
			image.compress(Bitmap.CompressFormat.JPEG, 70, os);
			image.recycle();
			try {
				os.close();
			} catch (IOException e) {
				Log.i("EX IO", e.toString());
			}
		} catch (FileNotFoundException e) {
			Log.i("EX NEW FILE", e.toString());
		}
	}
	
	public void addPendingInvite(String username){
		pendingInvites.add(username);
	}
	
	private void processMessage(String message){
		try {
			JSONObject msgObj = new JSONObject(message);
			
			String type = msgObj.getString("type");

			if (type.equals("chat")) {
				updateChat(new ChatUpdate(msgObj));
//				TODO: GET INVITE NOTIFICATIONS WHILE APP IS IN BACKGROUND
//			} else if (type.equals("invite")) {
//				applyUpdate(new InviteUpdate(msgObj));
			} else if (type.equals("newgroup")) {
				updateGroupMembers(new GroupUpdate(msgObj));
			}
			else if(type.equals("imagedownload")) {
				saveImageFile(new ImageDownloadUpdate(msgObj));
			}
			else if(type.equals("search")){
				checkSearchImages(new SearchUpdate(msgObj));
			}

		} catch (JSONException e) {
			Log.i("EX_SERVICE", e.toString());
		}
	}
	
    class MySocketClient extends WebSocketClient {
    	
		public MySocketClient(URI serverURI) {
			super(serverURI);
		}

		@Override
		public void onOpen(ServerHandshake handshakedata) {
			
		}

		@Override
		public void onMessage(String message) {
			processMessage(message);
			Intent intent = new Intent(INTENT_MESSAGE);
			intent.putExtra("message", message);
			LocalBroadcastManager.getInstance(ConnectionService.this).sendBroadcast(intent);	
		}

		@Override
		public void onClose(int code, String reason, boolean remote) {
			
		}

		@Override
		public void onError(Exception ex) {
			
		}	
    }

}
