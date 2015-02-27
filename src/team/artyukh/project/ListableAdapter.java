package team.artyukh.project;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.R;
import team.artyukh.project.lists.IListable;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListableAdapter extends ArrayAdapter<IListable> {
	
	private BindingActivity parent;
	private final ArrayList<IListable> list;
	private static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();
	static class ViewHolder {
		public TextView title;
		public TextView body;
		public ImageView icon;
		public Button one;
		public Button two;
	}
	
	public ListableAdapter(BindingActivity context, ArrayList<IListable> list){
		super(context, R.layout.rowview_listable, list);
		
		this.parent = context;
		this.list = list;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parentGroup) {
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = parent.getLayoutInflater();
			row = inflater.inflate(R.layout.rowview_listable, null);

			ViewHolder holder = new ViewHolder();
			holder.title = (TextView) row.findViewById(R.id.tvTitle);
			holder.body = (TextView) row.findViewById(R.id.tvBody);
			holder.icon = (ImageView) row.findViewById(R.id.ivIcon);
			holder.one = (Button) row.findViewById(R.id.btnActionOne);
			holder.two = (Button) row.findViewById(R.id.btnActionTwo);
			
			row.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) row.getTag();
		
		
		holder.title.setText(list.get(pos).getTitle());
		holder.body.setText(list.get(pos).getBody());
		
		Bitmap image = bitmapHash.get(list.get(pos).getId());
		if(image != null){
			holder.icon.setImageBitmap(image);
		}
		else{
			holder.icon.setImageResource(R.drawable.icon_person);
			image = BindingActivity.getBitmap(parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES), list.get(pos).getId());
			if(image != null){
				bitmapHash.put(list.get(pos).getId(), image);
				holder.icon.setImageBitmap(image);
			}		
		}
//		Log.i("HASH SIZE", bitmapHash.size() + "");
		
		if(list.get(pos).getType().equals("person")){
			
			holder.one.setText("Profile");
			holder.two.setText("Invite");
			holder.one.setOnClickListener(openProfile(pos));
			holder.two.setOnClickListener(sendInvite(pos));
		}
		else if(list.get(pos).getType().equals("marker")){
			holder.one.setText("Info");
			holder.two.setText("Directions");
		}
		else{
			holder.one.setVisibility(View.INVISIBLE);
			holder.two.setVisibility(View.INVISIBLE);
		}

		return row;
	}
	
	public static void saveNewBitmap(String objectId, Bitmap bmp){
		Log.i("NEW BITMAP", objectId);
		bitmapHash.put(objectId, bmp);
	}
	
	public int getSize(){
		return list.size();
	}
	
	private OnClickListener openProfile(final int pos){
		OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parent, OtherProfileActivity.class);
				intent.putExtra("userid", list.get(pos).getId());
				parent.startActivity(intent);
			}
		};
		
		return listener;
	}
	
	private OnClickListener sendInvite(final int pos){
		OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parent.sendInvite(list.get(pos).getTitle());				
			}
		};
		
		return listener;
	}
}
