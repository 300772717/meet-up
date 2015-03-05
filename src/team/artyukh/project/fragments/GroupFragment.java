package team.artyukh.project.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.ListableAdapter;
import team.artyukh.project.R;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.lists.Person;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GroupFragment extends Fragment implements OnClickListener {

	private View root;
	private ListableFragment mainFrag;
	private Button newGroup, leave;
    private TextView noGroup;
    private BindingActivity parent = null;
    private ListableAdapter adapter = null;
    
    @Override
    public void onCreate(Bundle savedInstance){
    	super.onCreate(savedInstance);
    	
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	
    	
    }
    
    public void configureViews(){   	
		if (isAdded()) {
			if (BindingActivity.getStringPref(BindingActivity.PREF_GROUP)
					.equals("")) {
				setNoGroupMode();
			} else {
				setGroupMode();
			}
		}
	}
    
    public void refreshViews(){
		if (isAdded()) {
			mainFrag.refreshViews();
		}
	}
    
    public void setMemberAdapter(ArrayList<IListable> members, BindingActivity activity){
    	Log.i("GROUP MEMBER# ", members.size() + "");
    	adapter = new ListableAdapter(activity, members, false);
    	showMembers();
    }
    
    private void showMembers(){
    	if(adapter != null && mainFrag != null){
    		mainFrag.setAdapter(adapter);
    	}
    }
    
    private void getGroupPrefs(){
    	
    	try {
			JSONArray members = new JSONArray(BindingActivity.getStringPref(BindingActivity.PREF_GROUP_MEMBERS));
			JSONObject member;
			ArrayList<IListable> adapterList = new ArrayList<IListable>();
			Log.i("MEMBERS", members.toString());
			for(int i = 0; i < members.length(); i++){
				member = members.getJSONObject(i);
				adapterList.add(new Person(member.getString("username"), 
						member.getString("status"), 
						member.getString("id"), 
						member.getString("picDate")));
			}
			adapter = new ListableAdapter(parent, adapterList, false);
		} catch (JSONException e) {
			Log.i("EX_GROUP", e.toString());
		}
    }
    
    @Override
	public void onResume(){
		super.onResume();
		showMembers();
	}
    
    private void setNoGroupMode(){	
	    noGroup.setVisibility(View.VISIBLE);
		newGroup.setVisibility(View.VISIBLE);
		leave.setVisibility(View.INVISIBLE);
		getChildFragmentManager().beginTransaction().hide(mainFrag).commit();
	}
	
	private void setGroupMode(){
		noGroup.setVisibility(View.INVISIBLE);
		newGroup.setVisibility(View.INVISIBLE);
		leave.setVisibility(View.VISIBLE);
		getChildFragmentManager().beginTransaction().show(mainFrag).commit();
	}
	
	@Override
	public void onClick(View v) {
		JSONObject grpObj = new JSONObject();
		switch(v.getId()){
		case R.id.btnLeave:
			try {
				grpObj.put("type", "leavegroup");
				grpObj.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			parent.send(grpObj.toString());
			BindingActivity.removePref(BindingActivity.PREF_CHAT);
			break;
		case R.id.btnCreateGroup:
			try {
				grpObj.put("type", "newgroup");
				grpObj.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			parent.send(grpObj.toString());
			BindingActivity.removePref(BindingActivity.PREF_CHAT);
			break;
		}
		
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_group, container, false);
		parent = (BindingActivity) getActivity();
		noGroup = (TextView) root.findViewById(R.id.tvNotGrouped);
		newGroup = (Button) root.findViewById(R.id.btnCreateGroup);
		leave = (Button) root.findViewById(R.id.btnLeave);
		
		leave.setOnClickListener(this);
		newGroup.setOnClickListener(this);
		
		mainFrag = new ListableFragment(parent);
    	FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_group, mainFrag).commit();
        getGroupPrefs();
        configureViews();	
		
		return root;
	}
	
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public GroupFragment() {
	}

}
