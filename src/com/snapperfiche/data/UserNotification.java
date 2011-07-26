package com.snapperfiche.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.snapperfiche.code.Enumerations;

public class UserNotification {
	private int id;
	private int notification_type;
	private String content_img_url;
	private String email;
	private String created_at;
	
	public UserNotification(){}
	
	public int getId(){
		return id;
	}
	
	public Enumerations.NotificationType getNotificationType(){
		return Enumerations.NotificationType.getType(this.notification_type);
	}
	
	public String getContentImage(){
		return content_img_url;
	}
	
	public String getEmail(){
		return email;
	}
	
	public Date getDate(){
		DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
		Date date = new Date();
		try {
			date = format.parse(created_at);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

}
	