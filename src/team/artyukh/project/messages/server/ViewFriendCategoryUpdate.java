package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.Person;
import android.util.Log;

public class ViewFriendCategoryUpdate {
	private String title = "";
	private ArrayList<IListable> friendList = new ArrayList<IListable>();

	public ViewFriendCategoryUpdate(JSONObject update) {
		try {
			title = update.getString("title");
			
			JSONArray friends = update.getJSONArray("friends");

			for (int i = 0; i < friends.length(); i++) {
				String username = friends.getJSONObject(i).getString("username");
				String id = friends.getJSONObject(i).getString("_id");
				String status = friends.getJSONObject(i).getString("status");
				String profilePicDate = friends.getJSONObject(i).getString("picDate");

				friendList.add(new Person(username, status, id, profilePicDate));
			}
		} catch (JSONException e) {
			Log.i("VIEW_FRIEND_CAT_EX", e.toString());
		}
	}
	
	public String getTitle(){
		return title;
	}

	public ArrayList<IListable> getFriends() {
		return friendList;
	}
}
