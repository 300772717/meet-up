package team.artyukh.project.lists;

public class MapMarker implements IListable {

	private String title;
	private String description;
	private String id;
	private String picDate;
	private String address;
	private boolean current;
	
	public MapMarker(String markerTitle, String markerDescription, String markerId, String date, String address, boolean isCurrent){
		this.title = markerTitle;
		this.description = markerDescription;
		this.id = markerId;
		this.picDate = date;
		this.current = isCurrent;
		this.address = address;
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
	public String getId() {
		return id;
	}

	@Override
	public String getImageDate() {
		return picDate;
	}

	@Override
	public int getType() {
		return IListable.LISTABLE_MARKER;
	}
	
	public boolean isCurrent(){
		return current;
	}
	
	public String getAddress(){
		return address;
	}
}
