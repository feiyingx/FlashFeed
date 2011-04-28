package com.snapperfiche.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.snapperfiche.code.Enumerations;

public class Post {
	private int id;
	private String post_type;
	private String username;
	private String photo_file_name;
	private String caption;
	private String created_at;
	
	public Post() {}
	
	public int getId(){
		return id;
	}
	
	public Enumerations.PostType getPostType(){
		return Enumerations.PostType.getPostType(post_type);
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPhotoUrl(){
		return photo_file_name;
	}
	
	public String getCaption(){
		return caption;
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
