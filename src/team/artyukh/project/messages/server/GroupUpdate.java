package team.artyukh.project.messages.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import team.artyukh.project.lists.IListable;
import team.artyukh.project.lists.Person;

public class GroupUpdate{
	private String groupId;
	private ArrayList<IListable> members = new ArrayList<IListable>();
	
	public GroupUpdate(JSONObject groupUpdate){
		try {
			this.groupId = groupUpdate.getString("group_id");
			JSONArray grpMembers = groupUpdate.getJSONArray("members");
			
			for(int i = 0; i < grpMembers.length(); i++){
				String username = grpMembers.getJSONObject(i).getString("username");
				String id = grpMembers.getJSONObject(i).getString("id");
				String status = grpMembers.getJSONObject(i).getString("status");
				String profilePicDate = grpMembers.getJSONObject(i).getString("picDate");
				
				members.add(new Person(username, status, id, profilePicDate));
			}
		} catch (JSONException e) {
			Log.i("GROUP_EX", e.toString());
		}
	}
	
	public String getGroupId(){
		return groupId;
	}
	
	public ArrayList<IListable> getMembers(){
		return members;
	}
}
