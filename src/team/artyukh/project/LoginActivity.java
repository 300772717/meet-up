package team.artyukh.project;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.messages.client.LoginRequest;
import team.artyukh.project.messages.server.LoginUpdate;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends BindingActivity {
	
	private String username, password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		this.getActionBar().setTitle("Login");
	}
	
	protected void applyUpdate(LoginUpdate update) {
		
		if(update.getStatus()){
			setPref(PREF_USERNAME, username);
			setPref(PREF_PASSWORD, password);
			setPref(PREF_USER_ID, update.getUserId());
			
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		else{
			AlertDialog.Builder b = new AlertDialog.Builder(this);
	        b.setTitle("WARNING");
			b.setMessage("Incorrect Login");
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
	
	public void login(View v){
		EditText usr = (EditText) findViewById(R.id.etUsername);
		EditText pass = (EditText) findViewById(R.id.etPassword);
		
		username = usr.getText().toString();
		password = pass.getText().toString();
						
		send(new LoginRequest(username, password).toString());
	}
	
	public void register(View v){
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
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
