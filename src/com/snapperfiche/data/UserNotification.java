package com.snapperfiche.data;

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
}
	