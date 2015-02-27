package team.artyukh.project.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.ListableAdapter;
import team.artyukh.project.R;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.messages.client.SearchRequest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;


public class SearchFragment extends Fragment {
	
	private View root;
	private BindingActivity parent;
	private Button search, back;
	private EditText input;
    ListableFragment mainFrag;
    ListableAdapter mAdapter = null;
	private boolean showingResults = false;
	
	@Override
    public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		setAdapter(mAdapter);
		setViews();
	}
	
	public void setResult(BindingActivity parent, ArrayList<IListable> adapterList){
		setAdapter(new ListableAdapter(parent, adapterList));
		showingResults = true;
	}
	
	private void setAdapter(ListableAdapter adapter){
		mAdapter = adapter;
		if (isAdded() && adapter != null) {
			mainFrag.setAdapter(adapter);
		}
	}
	
	public void startSearch(){
		parent.send(new SearchRequest(input.getText().toString()).toString());
		showingResults = true;
		setViews();
	}
	
	public void backToSearch(){
		showingResults = false;
		setViews();
	}
	
	private void setViews(){
		if(showingResults){
			search.setVisibility(View.INVISIBLE);
			input.setVisibility(View.INVISIBLE);
			back.setVisibility(View.VISIBLE);
			getChildFragmentManager().beginTransaction().show(mainFrag).commit();
		}
		else{
			search.setVisibility(View.VISIBLE);
			input.setVisibility(View.VISIBLE);
			back.setVisibility(View.INVISIBLE);
			getChildFragmentManager().beginTransaction().hide(mainFrag).commit();
		}
	}
	
	public void refreshViews(){
		if (isAdded()) {
			mainFrag.refreshViews();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_search, container, false);
		parent = (BindingActivity) getActivity();
		search = (Button) root.findViewById(R.id.btnStartSearch);
		back = (Button) root.findViewById(R.id.btnBackToSearch);
		input = (EditText) root.findViewById(R.id.etInput);
		
		input.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				view.requestFocusFromTouch();
				return false;
			}
		});
		
		mainFrag = new ListableFragment(parent);
    	FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_search, mainFrag).commit();	
		setViews();
		
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
	
	public SearchFragment() {
	}

}
