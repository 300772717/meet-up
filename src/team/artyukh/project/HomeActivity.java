package team.artyukh.project;

import java.util.ArrayList;

import team.artyukh.project.fragments.ChatFragment;
import team.artyukh.project.fragments.GroupFragment;
import team.artyukh.project.fragments.MapViewFragment;
import team.artyukh.project.fragments.SearchFragment;
import team.artyukh.project.lists.DrawerItemClickListener;
import team.artyukh.project.messages.server.ChatUpdate;
import team.artyukh.project.messages.server.GroupUpdate;
import team.artyukh.project.messages.server.ImageDownloadUpdate;
import team.artyukh.project.messages.server.MapUpdate;
import team.artyukh.project.messages.server.SearchUpdate;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class HomeActivity extends BindingActivity {

	MapViewFragment fragMap = new MapViewFragment();
	GroupFragment fragGroup =  new GroupFragment();
	ChatFragment fragChat = new ChatFragment();
	SearchFragment fragSearch = new SearchFragment();
	
	private ViewPager myPager;
	private DrawerLayout mDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,         
                R.drawable.icon_drawer,  
                R.string.accept,  
                R.string.accept  
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("Activity");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Navigation");
            }
        };
        
        mDrawer.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ArrayList<String> listItems = new ArrayList<String>();
        listItems.add("My Profile");
        listItems.add("My Places");
        listItems.add("Friends");
        listItems.add("Settings");
        //ADD DRAWER LIST ADAPTER
        
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));



		
		myPager = (ViewPager) findViewById(R.id.pager);
		
		MyPagerAdapter myAdapter = new MyPagerAdapter(getSupportFragmentManager());
		myAdapter.addFragment(fragMap, "Map");
		myAdapter.addFragment(fragChat, "Chat");
		myAdapter.addFragment(fragGroup, "Meeting");
		myAdapter.addFragment(fragSearch, "Search");
		myPager.setAdapter(myAdapter);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    protected void applyUpdate(MapUpdate message){ 	
    	fragMap.updateMap(message);
    }
    
    protected void applyUpdate(SearchUpdate message){
		fragSearch.setResult(HomeActivity.this, message.getResult());
		myPager.setCurrentItem(3);
	}
    
    protected void applyUpdate(GroupUpdate message){
    	fragGroup.setMemberAdapter(message.getMembers(), this);
		fragGroup.configureViews();
		fragChat.restoreChat();
	}
    
    protected void applyUpdate(ChatUpdate message){
    	fragChat.restoreChat();
	}
    
    protected void applyUpdate(ImageDownloadUpdate message){
		super.applyUpdate(message);
		
		fragChat.refreshViews();
		fragGroup.refreshViews();
		fragSearch.refreshViews();
	}
    
    public void startSearch(View v){
		fragSearch.startSearch();
	}
	
	public void backToSearch(View v){
		fragSearch.backToSearch();
	}
    
    public void sendMessage(View v){
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
    	int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}