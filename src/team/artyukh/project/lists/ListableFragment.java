package team.artyukh.project.lists;

import java.util.ArrayList;

import team.artyukh.project.BindingActivity;
import team.artyukh.project.ListableAdapter;
import team.artyukh.project.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class ListableFragment extends Fragment {

	private ListView list;
	private int size = 0;
	private ListableAdapter mAdapter;
	private BindingActivity mParent;
	
	public ListableFragment(){
	}
	
	public ListableFragment(BindingActivity parent){
		mParent = parent;
		mAdapter = new ListableAdapter(parent, new ArrayList<IListable>());
	}
	
	public void setAdapter(ListableAdapter adapter){
		list.setAdapter(adapter);
		this.mAdapter = adapter;
		if(adapter != null){
			size = adapter.getSize();
		}
		else{
			size = 0;
		}
		scrollBottom();
	}
	
	public void clearAdapter(){
		mAdapter = new ListableAdapter(mParent, new ArrayList<IListable>());
		list.setAdapter(mAdapter);
	}
	
	public ListableAdapter getAdapter(){
		return this.mAdapter;
	}
	
	public void scrollBottom(){
		list.setSelection(mAdapter.getSize());
	}
	
	public void refreshViews(){
		int index = list.getFirstVisiblePosition();
		View v = list.getChildAt(0);
		int top = (v == null) ? 0 : (v.getTop() - list.getPaddingTop());
		
		list.setAdapter(mAdapter);
		
		list.setSelectionFromTop(index, top);
	}
	
//	public void updateImage(String title, String encodedImage){
//		ArrayList<Integer> index = new ArrayList<Integer>();
//		
//		for(int i = 0; i < mAdapter.getSize(); i++){
//			IListable item = mAdapter.getItem(i);
//			if(item.getTitle().equals(title)){
//				index.add(i);
//			}
//		}
//		int first = list.getFirstVisiblePosition();
//	    int last = first + list.getChildCount() - 1;
//	    View rowView = new View(mParent);
//	    
//	    for(int pos : index){
//	    	if (pos < first || pos > last ) {
//	    		rowView = list.getAdapter().getView(pos, null, list);
//		    } else {
//		        int childIndex = pos - first;
//		        rowView = list.getChildAt(childIndex);
//		    }
//	    }
//	    
//	    ListableAdapter.ViewHolder holder = (ViewHolder) rowView.getTag();
//	    try {
//			byte[] data = Base64.decode(encodedImage);
//			Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
//			holder.icon.setImageBitmap(image);
//		} catch (IOException e) {
//
//		}
//	}
	
	@Override
	public void onStart(){
		
		super.onStart();
		list = (ListView) getView().findViewById(R.id.list);
		list.setAdapter(mAdapter);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listable, container, false); 
    }
}
