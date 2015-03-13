package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.Person;
import android.util.Log;

public class ViewFriendsUpdate {
	private ArrayList<IListable> friendList = new ArrayList<IListable>();

	public ViewFriendsUpdate(JSONObject update) {
		try {
			
			JSONArray friends = update.getJSONArray("friends");

			for (int i = 0; i < friends.length(); i++) {
				String username = friends.getJSONObject(i).getString("username");
				String id = friends.getJSONObject(i).getString("id");
				String status = friends.getJSONObject(i).getString("status");
				String profilePicDate = friends.getJSONObject(i).getString("picDate");
				String online = friends.getJSONObject(i).getString("online");

				friendList.add(new Person(username, status, id, profilePicDate, online));
			}
		} catch (JSONException e) {
			Log.i("VIEW_FRIEND_EX", e.toString());
		}
	}

	public ArrayList<IListable> getFriends() {
		return friendList;
	}
}
