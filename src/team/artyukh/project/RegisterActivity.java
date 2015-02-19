package team.artyukh.project;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.messages.server.RegisterUpdate;
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
		
	}
	
	public void cancel(View v){
		finish();
	}
	
	public void register(View v){
		JSONObject regObj = new JSONObject();
		EditText usr = (EditText) findViewById(R.id.etNewUsername);
		EditText pass = (EditText) findViewById(R.id.etNewPassword);
		EditText passRep = (EditText) findViewById(R.id.etNewPasswordRepeat);
		
		if(!pass.getText().toString().equals(passRep.getText().toString())){
			//HANDLE INPUT ERRORS
			return;
		}
		
		try {
			regObj.put("type", "register");
			regObj.put("username", usr.getText().toString());
			regObj.put("password", pass.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		send(regObj.toString());
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
