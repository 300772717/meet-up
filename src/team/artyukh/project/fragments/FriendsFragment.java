package team.artyukh.project.fragments;

import java.lang.reflect.Field;
import java.util.Locale;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.FriendsActivity;
import team.artyukh.project.ListableAdapter;
import team.artyukh.project.R;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.messages.client.RemoveFriendRequest;
import team.artyukh.project.messages.client.ViewFriendsRequest;
import team.artyukh.project.messages.server.ViewFriendsUpdate;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FriendsFragment extends Fragment {
	
	private TextView noFriends;
	private Button removeFriend;
	private EditText filterFriends;
	private ListableFragment mainFrag;
	private BindingActivity parent = null;
	
	private boolean serviceConnected = false;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.fragment_friends, container, false);
		parent = (BindingActivity) getActivity();
		
		if(serviceConnected){
			parent.send((new ViewFriendsRequest()).toString());
		}
		
		noFriends = (TextView) root.findViewById(R.id.tvNoFriends);
		removeFriend = (Button) root.findViewById(R.id.btnRemoveFriend);
		filterFriends = (EditText) root.findViewById(R.id.etFriendFilter);
		
		removeFriend.setOnClickListener(removeFriendListener);
		filterFriends.addTextChangedListener(filterWatcher);

		mainFrag = new ListableFragment(parent);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container_friends, mainFrag).commit();
		mainFrag.makeSelectable();

		getChildFragmentManager().beginTransaction().hide(mainFrag).commit();
		noFriends.setVisibility(View.INVISIBLE);
		removeFriend.setVisibility(View.INVISIBLE);
		
		return root;
	}
	
	public void serviceConnected(){
		serviceConnected = true;
		
		if(parent != null){
			parent.send((new ViewFriendsRequest()).toString());
		}
	}
	
	public void setFriendList(ViewFriendsUpdate message){
		if(message.getFriends().size() > 0){
			mainFrag.setAdapter(new ListableAdapter(parent, message.getFriends(), true));
			getChildFragmentManager().beginTransaction().show(mainFrag).commit();
	        noFriends.setVisibility(View.INVISIBLE);
	        removeFriend.setVisibility(View.VISIBLE);
	        filterFriends.setVisibility(View.VISIBLE);
	        mainFrag.getAdapter().filter(filterFriends.getText().toString().toLowerCase(Locale.getDefault()));
		}
		else{
			getChildFragmentManager().beginTransaction().hide(mainFrag).commit();
	        noFriends.setVisibility(View.VISIBLE);
	        removeFriend.setVisibility(View.INVISIBLE);
	        filterFriends.setVisibility(View.INVISIBLE);
		}
	}
	
	OnClickListener removeFriendListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int position = mainFrag.getSelectedItemPosition();
			
			if(position >= 0){
				IListable friend = mainFrag.getAdapter().getItem(position);
				parent.send((new RemoveFriendRequest(friend.getId())).toString());
			}
			
		}
		
	};
	
	TextWatcher filterWatcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int count, int after) {
			mainFrag.getAdapter().filter(s.toString().toLowerCase(Locale.getDefault()));
		}
		
	};
	
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
	
	public FriendsFragment() {
	}

}
