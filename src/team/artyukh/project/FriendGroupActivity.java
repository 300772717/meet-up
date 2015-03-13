package team.artyukh.project;

import java.util.ArrayList;
import java.util.Locale;

import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.messages.client.RemoveCategoryRequest;
import team.artyukh.project.messages.client.RemoveFriendRequest;
import team.artyukh.project.messages.client.RemoveFromCategoryRequest;
import team.artyukh.project.messages.client.ViewFriendCategoryRequest;
import team.artyukh.project.messages.server.ViewFriendCategoryUpdate;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FriendGroupActivity extends BindingActivity {

	private ListableFragment mainFrag;
	private Button addFriend;
	private Button removeFriend;
	private TextView noFriends;
	private static String catid;
	private String catname = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_group);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			catid = extras.getString("catid");
		}

		addFriend = (Button) findViewById(R.id.btnAddFriendGroup);
		removeFriend = (Button) findViewById(R.id.btnRemoveGroupFriend);
		noFriends = (TextView) findViewById(R.id.tvNoFriendsInCat);
		
		addFriend.setOnClickListener(AddFriendListener);
		removeFriend.setOnClickListener(RemoveFriendListener);

		mainFrag = new ListableFragment(FriendGroupActivity.this);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container_category, mainFrag).commit();
		mainFrag.makeSelectable();
	}
	
	@Override
	public void onServiceConnected(){
		send(new ViewFriendCategoryRequest(catid).toString());
	}
	
	@Override
	protected void applyUpdate(ViewFriendCategoryUpdate message){
		getActionBar().setTitle(message.getTitle());
		catname = message.getTitle();
		if(message.getFriends().size() > 0){
			mainFrag.setAdapter(new ListableAdapter(FriendGroupActivity.this, message.getFriends(), true));
			getSupportFragmentManager().beginTransaction().show(mainFrag).commit();
	        noFriends.setVisibility(View.INVISIBLE);
	        removeFriend.setVisibility(View.VISIBLE);
		}
		else{
			getSupportFragmentManager().beginTransaction().hide(mainFrag).commit();
	        noFriends.setVisibility(View.VISIBLE);
	        removeFriend.setVisibility(View.INVISIBLE);
		}
	}
	
	OnClickListener RemoveFriendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = mainFrag.getSelectedItemPosition();

			if (position >= 0) {
				IListable friend = mainFrag.getAdapter().getItem(position);
				send(new RemoveFromCategoryRequest(catid, friend.getId()).toString());
			}			
		}
	};
	
	OnClickListener AddFriendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(FriendGroupActivity.this,
					SelectFriendActivity.class);
			intent.putExtra("catid", catid);
			intent.putExtra("catname", catname);
			
			int size = mainFrag.getAdapter().getSize();
			ArrayList<String> except = new ArrayList<String>();
			for(int i = 0; i < size; i++){
				IListable friend = mainFrag.getAdapter().getItem(i);
				except.add(friend.getId());
			}
			
			intent.putExtra("except", except);
			startActivity(intent);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.friend_group, menu);
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
