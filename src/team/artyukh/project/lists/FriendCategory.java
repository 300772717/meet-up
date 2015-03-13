package team.artyukh.project.lists;

public class FriendCategory implements IListable {

	private String title;
	private String description;
	private String id;
	
	public FriendCategory(String title, String id, int count){
		this.title = title;
		this.id = id;
		this.description = count + " friend";
		if(count != 1){
			this.description = this.description.concat("s");
		}
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getBody() {
		return description;
	}

	@Override
	public int getType() {
		return LISTABLE_CATEGORY;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getImageDate() {
		return null;
	}

}
