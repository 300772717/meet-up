package team.artyukh.project.lists;

import team.artyukh.project.MyPlacesActivity;
import team.artyukh.project.MyProfileActivity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerItemClickListener implements ListView.OnItemClickListener {
	
	private Context mContext;
	
	public DrawerItemClickListener(Context context){
		this.mContext = context;
	}
	
	@Override
	public void onItemClick(AdapterView parent, View view, int pos, long id) {
		Intent intent = new Intent();
		switch (pos) {
		case 0:
			intent.setClass(mContext, MyProfileActivity.class);
			mContext.startActivity(intent);
			break;
		case 1:
			intent.setClass(mContext, MyPlacesActivity.class);
			mContext.startActivity(intent);
			break;
		}
		
	}

}
