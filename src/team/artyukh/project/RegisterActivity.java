package team.artyukh.project;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.messages.client.RegisterRequest;
import team.artyukh.project.messages.server.RegisterUpdate;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends BindingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}
	
	protected void applyUpdate(RegisterUpdate update) {
		if(update.getStatus()){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		else{
			AlertDialog.Builder b = new AlertDialog.Builder(this);
	        b.setTitle("WARNING");
			b.setMessage("An account with that username already exists");
	        b.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

	        AlertDialog confirm = b.create();
	        confirm.show();
		}
		
	}
	
	public void cancel(View v){
		finish();
	}
	
	public void register(View v){
		EditText usr = (EditText) findViewById(R.id.etNewUsername);
		EditText pass = (EditText) findViewById(R.id.etNewPassword);
		EditText passRep = (EditText) findViewById(R.id.etNewPasswordRepeat);
		
		if(!pass.getText().toString().equals(passRep.getText().toString())){
			AlertDialog.Builder b = new AlertDialog.Builder(this);
	        b.setTitle("WARNING");
			b.setMessage("The passwords don't match");
	        b.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

	        AlertDialog confirm = b.create();
	        confirm.show();
			return;
		} else if(pass.getText().toString().length() < 6){
			AlertDialog.Builder b = new AlertDialog.Builder(this);
	        b.setTitle("WARNING");
			b.setMessage("A password must be at least 6 characters in length");
	        b.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

	        AlertDialog confirm = b.create();
	        confirm.show();
	        return;
		}
		
		send(new RegisterRequest(usr.getText().toString(), pass.getText().toString()).toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.register, menu);
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
