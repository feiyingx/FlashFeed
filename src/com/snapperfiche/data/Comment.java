package com.snapperfiche.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Comment {
	private int id;
	private int post_id;
	private int user_id;
	private String email;
	private String content;
	private String created_at;
	private String updated_at;
	
	public Comment(){}
	
	public int getId(){
		return id;
	}
	
	public int getPostId(){
		return post_id;
	}
	
	public int getUserId(){
		return user_id;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getContent(){
		return content;
	}
	
	public Date getCreateDate(){
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
	
	public Date getUpdateDate(){
		DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
		Date date = new Date();
		try {
			date = format.parse(updated_at);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
}
