package team.artyukh.project.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.ListableAdapter;
import team.artyukh.project.R;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.messages.client.NewCategoryRequest;
import team.artyukh.project.messages.client.RemoveCategoryRequest;
import team.artyukh.project.messages.client.ViewFriendsRequest;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FriendGroupFragment extends Fragment {
	
	private TextView noCats;
	private Button removeCat;
	private Button newCat;
	private ListableFragment mainFrag;
	private BindingActivity parent;
	private ListableAdapter adapter = null;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_friend_group, container, false);
		parent = (BindingActivity) getActivity();
		
		noCats = (TextView) root.findViewById(R.id.tvNoCats);
		newCat = (Button) root.findViewById(R.id.btnMakeCat);
		removeCat = (Button) root.findViewById(R.id.btnRemoveCat);
		
		newCat.setOnClickListener(NewCategoryListener);
		removeCat.setOnClickListener(RemoveCategoryListener);
		
		mainFrag = new ListableFragment(parent);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container_friend_groups, mainFrag).commit();
		mainFrag.makeSelectable();
		
		configureViews();
		
		return root;
	}
	
	public void setListAdapter(ArrayList<IListable> cats, BindingActivity activity) {
		adapter = new ListableAdapter(activity, cats, true);
		showList();
	}

	private void showList() {
		configureViews();
		if (adapter != null && mainFrag != null) {
			mainFrag.setAdapter(adapter);
			mainFrag.getAdapter().notifyDataSetChanged();
		}
	}
	
	private void configureViews(){
		if(!isAdded() && !isVisible()) return;
		if(adapter == null){
			hideViews();
			return;
		}
		if(adapter.getSize() <= 0){
			 hideViews();
		}
		else{
			showViews();
		}
		
	}
	
	private void hideViews(){
		noCats.setVisibility(View.VISIBLE);
		removeCat.setVisibility(View.INVISIBLE);
		getChildFragmentManager().beginTransaction().hide(mainFrag).commit();
	}
	
	private void showViews(){
		noCats.setVisibility(View.INVISIBLE);
		removeCat.setVisibility(View.VISIBLE);
		getChildFragmentManager().beginTransaction().show(mainFrag).commit();
	}
	
	OnClickListener RemoveCategoryListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int position = mainFrag.getSelectedItemPosition();
			if(position >= 0){
				IListable cat = mainFrag.getAdapter().getItem(position);
				parent.send(new RemoveCategoryRequest(cat.getId()).toString());		
			}		
		}
		
	};
	
	OnClickListener NewCategoryListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			final EditText input = new EditText(parent);
			
			new AlertDialog.Builder(parent)
			  .setTitle("WARNING")
			  .setMessage("Choose new category name:")
			  .setView(input)
			  .setPositiveButton("Create", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			      String name = input.getText().toString();
			      parent.send(new NewCategoryRequest(name).toString());
			    }
			  })
			  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	dialog.cancel();
			    }
			  })
			  .show();			
		}
		
	};
	
	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public FriendGroupFragment() {
		// Required empty public constructor
	}

}
