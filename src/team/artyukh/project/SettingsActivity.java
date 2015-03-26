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
	private CheckBox cbMuteSound;
	private CheckBox cbBlockMessages;
	private CheckBox cbBlockInvites;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		btnApply = (Button) findViewById(R.id.btnApplySettings);
		cbAppearOffline = (CheckBox) findViewById(R.id.cbAppearOffline);
		cbMuteSound = (CheckBox) findViewById(R.id.cbMuteSound);
		cbBlockMessages = (CheckBox) findViewById(R.id.cbBlockMessages);
		cbBlockInvites = (CheckBox) findViewById(R.id.cbBlockInvites);
		
		boolean aprOffline = Boolean.parseBoolean(getStringPref(PREF_APPEAR_OFFLINE));
		boolean muteSound = Boolean.parseBoolean(getStringPref(PREF_MUTE_SOUND));
		boolean blockMessages = Boolean.parseBoolean(getStringPref(PREF_BLOCK_MESSAGES));
		boolean blockInvites = Boolean.parseBoolean(getStringPref(PREF_BLOCK_INVITES));
		
		cbAppearOffline.setChecked(aprOffline);
		cbMuteSound.setChecked(muteSound);
		cbBlockMessages.setChecked(blockMessages);
		cbBlockInvites.setChecked(blockInvites);
		
		btnApply.setOnClickListener(ApplyListener);
	}
	
	OnClickListener ApplyListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			ModifyProfileRequest request = new ModifyProfileRequest();
			request.setAppearOffline(cbAppearOffline.isChecked());
			request.setMuteSound(cbMuteSound.isChecked());
			request.setBlockMessages(cbBlockMessages.isChecked());
			request.setBlockInvites(cbBlockInvites.isChecked());
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
