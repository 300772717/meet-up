package team.artyukh.project;

import team.artyukh.project.messages.client.ModifyProfileRequest;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingsActivity extends BindingActivity {
	
	private Button btnApply;
	private CheckBox cbAppearOffline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		btnApply = (Button) findViewById(R.id.btnApplySettings);
		cbAppearOffline = (CheckBox) findViewById(R.id.cbAppearOffline);
		
		boolean aprOffline = Boolean.parseBoolean(getStringPref(PREF_APPEAR_OFFLINE));
		cbAppearOffline.setChecked(aprOffline);
		
		btnApply.setOnClickListener(ApplyListener);
	}
	
	OnClickListener ApplyListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			ModifyProfileRequest request = new ModifyProfileRequest();
			request.setAppearOffline(cbAppearOffline.isChecked());
			send(request.toString());
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
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
