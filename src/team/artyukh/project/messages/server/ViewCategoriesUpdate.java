package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.artyukh.project.lists.FriendCategory;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.Person;
import android.util.Log;

public class ViewCategoriesUpdate {
	private ArrayList<IListable> categoryList = new ArrayList<IListable>();

	public ViewCategoriesUpdate(JSONObject update) {
		try {
			
			JSONArray cats = update.getJSONArray("cats");

			for (int i = 0; i < cats.length(); i++) {
				String title = cats.getJSONObject(i).getString("title");
				String id = cats.getJSONObject(i).getString("id");
				int count = cats.getJSONObject(i).getInt("count");

				categoryList.add(new FriendCategory(title, id, count));
			}
		} catch (JSONException e) {
			Log.i("VIEW_CAT_EX", e.toString());
		}
	}

	public ArrayList<IListable> getCategories() {
		return categoryList;
	}
}
