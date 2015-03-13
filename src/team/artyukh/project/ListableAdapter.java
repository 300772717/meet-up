package team.artyukh.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.R;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.MapMarker;
import team.artyukh.project.messages.client.HideMarkerRequest;
import team.artyukh.project.messages.client.PersonalMessageRequest;
import team.artyukh.project.messages.client.SetMarkerRequest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ListableAdapter extends ArrayAdapter<IListable> {
	
	private BindingActivity parent;
	private ArrayList<IListable> list = new ArrayList<IListable>();
	private ArrayList<IListable> mainList = new ArrayList<IListable>();
	private static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();
	private boolean select = false;
	static class ViewHolder {
		public TextView title;
		public TextView body;
		public ImageView icon;
		public Button one;
		public Button two;
		public Button three;
	}
	
	public ListableAdapter(BindingActivity context, ArrayList<IListable> list, boolean selectable){
		super(context, R.layout.rowview_listable, list);
		
		this.parent = context;
		if(list != null){
			this.list = list;
			this.mainList.addAll(list);
		}
		this.select = selectable;
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
			holder.three = (Button) row.findViewById(R.id.btnActionThree);
			
			row.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) row.getTag();
		int drawable = R.drawable.icon_person;
		
		holder.title.setText(list.get(pos).getTitle());
		holder.body.setText(list.get(pos).getBody());
		
		if (list.get(pos).getType() == IListable.LISTABLE_PERSON) {
			holder.one.setText("Profile");
			holder.two.setText("Invite");
			holder.three.setText("Message");
			holder.one.setOnClickListener(openProfile(pos));
			holder.two.setOnClickListener(sendInvite(pos));
			holder.three.setOnClickListener(sendMessage(pos));
		} else if (list.get(pos).getType() == IListable.LISTABLE_MARKER) {
//			holder.icon.setImageResource(R.drawable.icon_marker);
			drawable = R.drawable.icon_marker;
			if(((MapMarker)list.get(pos)).isCurrent()){
				holder.one.setText("Hide");
				holder.one.setOnClickListener(hideMarker(pos));
			}
			else{
				holder.one.setText("Show on Map");
				holder.one.setOnClickListener(setCurrentMarker(pos));
			}
			
			holder.two.setText("Edit");
			holder.three.setVisibility(View.INVISIBLE);
			
		} else {
			holder.one.setVisibility(View.INVISIBLE);
			holder.two.setVisibility(View.INVISIBLE);
			holder.three.setVisibility(View.INVISIBLE);
		}
		
		Bitmap image = bitmapHash.get(list.get(pos).getId());
		if(image != null){
			holder.icon.setImageBitmap(image);
		}
		else{
			image = BindingActivity.getBitmap(parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES), list.get(pos).getId());
			if(image != null){
				bitmapHash.put(list.get(pos).getId(), image);
				holder.icon.setImageBitmap(image);
			}
			else{
				holder.icon.setImageResource(drawable);
			}
		}
		if(select){
			holder.one.setFocusable(false);
			holder.two.setFocusable(false);
			row.setBackground(parent.getResources().getDrawable(R.drawable.ilistable_background));
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
	
	public void filter(String filter){
		list.clear();
		if (filter.length() == 0) {
			list.addAll(mainList);
			
		} else {
			for (IListable il : mainList) {
				if (il.getTitle().toLowerCase(Locale.getDefault()).contains(filter)) {
					list.add(il);
				}
			}
		}
		notifyDataSetChanged();
	}
	
	private OnClickListener setCurrentMarker(final int pos){
		OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parent.send(new SetMarkerRequest(list.get(pos).getId()).toString());
			}
		};
		
		return listener;
	}
	
	private OnClickListener hideMarker(final int pos){
		OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parent.send(new HideMarkerRequest(list.get(pos).getId()).toString());
			}
		};
		
		return listener;
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
	
	private OnClickListener sendMessage(final int pos){
		OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(parent);
				
				new AlertDialog.Builder(parent)
				  .setTitle("Send Message to " + list.get(pos).getTitle())
				  .setView(input)
				  .setPositiveButton("Send", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				      String msg = input.getText().toString();
				      parent.send(new PersonalMessageRequest(msg, list.get(pos).getTitle()).toString());
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
		
		return listener;
	}
}
