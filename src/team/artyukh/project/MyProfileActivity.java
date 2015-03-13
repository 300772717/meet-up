package team.artyukh.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import team.artyukh.project.messages.client.ImageUploadRequest;
import team.artyukh.project.messages.client.ModifyProfileRequest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileActivity extends BindingActivity {
	
	private TextView username;
	private EditText status;
	private ImageView profilePic;
	private static int RESULT_IMAGE = 123;
	private byte[] imgBytes = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		username = (TextView) findViewById(R.id.tvUsername);
		status = (EditText) findViewById(R.id.etStatus);
		profilePic = (ImageView) findViewById(R.id.ivProfilePicture);
		
		username.setText(getStringPref(PREF_USERNAME));
		status.setText(getStringPref(PREF_STATUS));
		
		Bitmap bmp;
		if((bmp = getBitmap(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getStringPref(PREF_USER_ID))) != null){
			profilePic.setImageBitmap(bmp);
		}
	}
	
	public void updateProfile(View v){
		ModifyProfileRequest modProfile = new ModifyProfileRequest();
		modProfile.setStatusMessage(status.getText().toString());
		send(modProfile.toString());
		
		if(imgBytes != null){
			send(new ImageUploadRequest(ImageUploadRequest.OBJ_PERSON, Base64.encodeToString(imgBytes, Base64.NO_WRAP)).toString());
			
			//REMOVE
			String data = Base64.encodeToString(imgBytes, Base64.NO_WRAP);
			byte[] image = Base64.decode(data, Base64.NO_WRAP);
			final BitmapFactory.Options options = new BitmapFactory.Options();
		    //options.inSampleSize = 16;
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeByteArray(image, 0, image.length, options);
		    Log.i("BOUNDS", "WIDE " + options.outWidth + " HIGH " + options.outHeight);
//			profilePic.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
			
			File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getStringPref(PREF_USERNAME));

		    try {
		        OutputStream os = new FileOutputStream(file);
		        os.write(imgBytes);
		        os.close();
		    } catch (IOException e) {
		        Log.i("ExternalStorage", "Error writing " + file, e);
		    }
		}
		
//		Log.i("MODIFY_PROFILE", modProfile.toString());
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
			Log.i("IMAGE DATA", data.getData().toString());
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Drawable drawable = profilePic.getDrawable();
			if (drawable instanceof BitmapDrawable) {
			    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			    Bitmap bitmap = bitmapDrawable.getBitmap();
			    bitmap.recycle();
			}
//			final BitmapFactory.Options options = new BitmapFactory.Options();
////		    options.inSampleSize = 16;
//		    options.inJustDecodeBounds = true;
//		    BitmapFactory.decodeFile(picturePath, options);
//		    options.inSampleSize = BindingActivity.calculateInSampleSize(options, 150, 150);
//		    
//		    options.inJustDecodeBounds = false;
//			Bitmap picture = BitmapFactory.decodeFile(picturePath, options);
			Bitmap picture = getBitmap(new File(picturePath));
			profilePic.setImageBitmap(picture);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    picture.compress(CompressFormat.JPEG, 70, stream);
		    imgBytes = stream.toByteArray();
//		    picture.recycle();
		    
//		    Log.i("IMAGE_BASE64", imgString);
//		    byte[] byteImage = Base64.decode(imgString, Base64.DEFAULT);
//		    picture = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
//		    profilePic.setImageBitmap(picture);
//		    send(new ImageUpdateRequest(imgString).toString());
//		    send(imgString);
//		    Log.i("IMAGE OBJ", new ImageUpdateRequest(imgString).toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    case R.id.action_settings:
	    	return true;
	    }
		
		return super.onOptionsItemSelected(item);
	}
}
