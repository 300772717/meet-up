package team.artyukh.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogWrapper {
	private AlertDialog.Builder builder;
	private String positiveButton = null;
	private String negativeButton = null;
	
	public DialogWrapper(Context context){
		builder = new AlertDialog.Builder(context);
		positiveButton = "OK";
		negativeButton = "Cancel";
	}
	
	public DialogWrapper(Context context, String title, String message){
		this(context);
		builder.setTitle(title);
		builder.setMessage(message);
	}
	
	public void setPositiveText(String text){
		positiveButton = text;
	}
	
	public void setNegativeText(String text){
		negativeButton = text;
	}
	
	public void setTitle(String title){
		builder.setTitle(title);
	}
	
	public void setMessage(String message){
		builder.setMessage(message);
	}
	
	public void setPositiveButton(DialogInterface.OnClickListener listener){
		builder.setPositiveButton(positiveButton, listener);
	}
	
	public void setNegativeButton(DialogInterface.OnClickListener listener){
		builder.setNegativeButton(negativeButton, listener);
	}
	
	public void show(){
		builder.create().show();
	}
}
