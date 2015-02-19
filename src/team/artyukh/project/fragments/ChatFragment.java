package team.artyukh.project.fragments;

import java.lang.reflect.Field;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.ListableAdapter;
import team.artyukh.project.R;
import team.artyukh.project.lists.ListableFragment;
import team.artyukh.project.lists.TextMessage;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ChatFragment extends Fragment implements OnClickListener {

	private View root;
	private ListableFragment mainFrag;
	private Button btnSend;
	private EditText et;
	private BindingActivity parent;
	
	public void sendMessage(){
		
		JSONObject chatObj = new JSONObject();
		
		try {
			chatObj.put("type", "chat");
			chatObj.put("group", BindingActivity.getStringPref(BindingActivity.PREF_GROUP));
			chatObj.put("username", BindingActivity.getStringPref(BindingActivity.PREF_USERNAME));
			chatObj.put("message", et.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		et.setText("");
		parent.send(chatObj.toString());
	}
	
	public void restoreChat(){
		try {
			String chat = BindingActivity.getStringPref(BindingActivity.PREF_CHAT);
			if(chat.equals("")) {
				mainFrag.clearAdapter();
				return;
			}
			
			JSONArray chatArr = new JSONArray(chat);
			
			if (mainFrag.getAdapter().getSize() > chatArr.length()){
				mainFrag.clearAdapter();
			}
			
			int startIndex = Math.max(0, mainFrag.getAdapter().getSize());

			for (int i = startIndex; i < chatArr.length(); i++) {
				JSONObject msg = chatArr.getJSONObject(i);

				mainFrag.getAdapter().add(new TextMessage(msg.getString("sender"), msg.getString("text"), msg.getString("id"), msg.getString("picDate")));
				mainFrag.scrollBottom();
			}
			
		} catch (JSONException e) {
			Log.i("EX", e.toString());
		}
	}
	
	public void refreshViews(){
		if (isAdded()) {
			mainFrag.refreshViews();
		}
	}
	
	@Override
	public void onClick(View v) {
		sendMessage();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		restoreChat();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_chat, container, false);
		parent = (BindingActivity) getActivity();
		mainFrag = new ListableFragment(parent);
    	FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_chat, mainFrag).commit();
		
        et = (EditText) root.findViewById(R.id.etMessage);
        btnSend = (Button) root.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        
        et.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				view.requestFocusFromTouch();
				return false;
			}
		});
        
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
	
	public ChatFragment() {
	}
}
