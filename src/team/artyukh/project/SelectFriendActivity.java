package team.artyukh.project;

import java.util.ArrayList;
import java.util.Locale;

import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.messages.client.AddToCategoryRequest;
import team.artyukh.project.messages.client.RemoveCategoryRequest;
import team.artyukh.project.messages.client.ViewFriendsRequest;
import team.artyukh.project.messages.server.ViewFriendsUpdate;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SelectFriendActivity extends BindingActivity {

	private ListableFragment mainFrag;
	private Button btnCancel;
	private Button btnAdd;
	private EditText etFilter;
	private TextView tvNoFriends;
	private String catid;
	private ArrayList<String> except = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_friend);
		
		Bundle extras = getIntent().getExtras();
		catid = extras.getString("catid");
		except = extras.getStringArrayList("except");
		getActionBar().setTitle(extras.getString("catname"));
		
		etFilter = (EditText) findViewById(R.id.etFilterSelectFriend);
		tvNoFriends = (TextView) findViewById(R.id.tvNoFriendsSelect);
		btnCancel = (Button) findViewById(R.id.btnCancelFriendSelect);
		btnAdd = (Button) findViewById(R.id.btnAddToCategory);
		
		btnCancel.setOnClickListener(CancelListener);
		btnAdd.setOnClickListener(AddListener);
		etFilter.addTextChangedListener(FilterWatcher);
		
		mainFrag = new ListableFragment(SelectFriendActivity.this);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container_select_friend, mainFrag).commit();
		mainFrag.makeSelectable();
	}
	
	@Override
	public void onServiceConnected(){
		send((new ViewFriendsRequest(except)).toString());
	}
	
	@Override
	protected void applyUpdate(ViewFriendsUpdate message){
		if(message.getFriends().size() > 0){
			mainFrag.setAdapter(new ListableAdapter(SelectFriendActivity.this, message.getFriends(), true));
			getSupportFragmentManager().beginTransaction().show(mainFrag).commit();
	        tvNoFriends.setVisibility(View.INVISIBLE);
	        btnAdd.setVisibility(View.VISIBLE);
	        mainFrag.getAdapter().filter(etFilter.getText().toString().toLowerCase(Locale.getDefault()));
		}
		else{
			getSupportFragmentManager().beginTransaction().hide(mainFrag).commit();
	        tvNoFriends.setVisibility(View.VISIBLE);
	        btnAdd.setVisibility(View.INVISIBLE);
		}
	}
	
	TextWatcher FilterWatcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			mainFrag.getAdapter().filter(etFilter.getText().toString().toLowerCase(Locale.getDefault()));			
		}
		
	};
	
	OnClickListener AddListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			int position = mainFrag.getSelectedItemPosition();
			if(position >= 0){
				IListable friend = mainFrag.getAdapter().getItem(position);
				send(new AddToCategoryRequest(catid, friend.getId()).toString());
				finish();
			}
		}
	};
	
	OnClickListener CancelListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_friend, menu);
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
