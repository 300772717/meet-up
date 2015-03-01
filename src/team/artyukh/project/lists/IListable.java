package team.artyukh.project.lists;

public interface IListable {
	public static final int LISTABLE_PERSON = 0;
	public static final int LISTABLE_MARKER = 1;
	public static final int LISTABLE_MESSAGE = 2;
	
	public String getTitle();
	public String getBody();
	public int getType();
	public String getId();
	public String getImageDate();
}
