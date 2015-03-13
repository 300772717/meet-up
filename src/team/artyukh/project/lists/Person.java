package team.artyukh.project.lists;


public class Person implements IListable {
	
	private String username;
	private String status;
	private String id;
	private String picDate;
	private boolean online;
	
	public Person(String userName, String statusMessage, String userId, String date, String online){
		this.username = userName;
		this.status = statusMessage;
		this.id = userId;
		this.picDate = date;
		this.online = Boolean.parseBoolean(online);
	}
	
	@Override
	public String getTitle() {
		return username;
	}

	@Override
	public String getBody() {
		return status;
	}

	@Override
	public int getType() {
		return IListable.LISTABLE_PERSON;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getImageDate() {
		return picDate;
	}
	
	public boolean isOnline(){
		return online;
	}

}
