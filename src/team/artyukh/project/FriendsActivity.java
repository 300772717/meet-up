package team.artyukh.project;

import java.util.ArrayList;
import java.util.Locale;

import team.artyukh.project.fragments.FriendGroupFragment;
import team.artyukh.project.fragments.FriendsFragment;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.messages.client.RemoveFriendRequest;
import team.artyukh.project.messages.client.ViewCategoriesRequest;
import team.artyukh.project.messages.client.ViewFriendsRequest;
import team.artyukh.project.messages.server.FriendIdUpdate;
import team.artyukh.project.messages.server.ViewCategoriesUpdate;
import team.artyukh.project.messages.server.ViewFriendsUpdate;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FriendsActivity extends BindingActivity {
	
	private ViewPager myPager;
	private FriendsFragment fragFriends = new FriendsFragment();
	private FriendGroupFragment fragGroups = new FriendGroupFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		myPager = (ViewPager) findViewById(R.id.friendPager);
		
		MyPagerAdapter myAdapter = new MyPagerAdapter(getSupportFragmentManager());
		myAdapter.addFragment(fragFriends, "Friends");
		myAdapter.addFragment(fragGroups, "Categories");
		myPager.setAdapter(myAdapter);
	}
	
	@Override
	protected void onServiceConnected(){
		send((new ViewCategoriesRequest()).toString());
		fragFriends.serviceConnected();
	}
	
	@Override
	protected void applyUpdate(ViewFriendsUpdate message){
		fragFriends.setFriendList(message);
	}
	
	@Override
	protected void applyUpdate(FriendIdUpdate message){
		send((new ViewFriendsRequest()).toString());
	}
	
	@Override
	protected void applyUpdate(ViewCategoriesUpdate message){
		fragGroups.setListAdapter(message.getCategories(), FriendsActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.friends, menu);
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
