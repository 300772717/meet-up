package team.artyukh.project.lists;

public class TextMessage implements IListable {
	
	private String sender;
	private String text;
	private String senderid;
	private String picDate;
	
	public TextMessage(String userName, String textMessage, String userId, String date){
		this.sender = userName;
		this.text = textMessage;
		this.senderid = userId;
		this.picDate = date;
	}
	
	@Override
	public String getTitle() {
		return sender;
	}

	@Override
	public String getBody() {
		return text;
	}

	@Override
	public int getType() {
		return IListable.LISTABLE_MESSAGE;
	}

	@Override
	public String getId() {
		return senderid;
	}
	
	@Override
	public String getImageDate() {
		return picDate;
	}

}
