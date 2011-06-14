package com.snapperfiche.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Tag {
	private int id;
	private int user_id;
	private String name;
	private String created_at;
	private int num_posts;
	
	public Tag(){}
	
	public Tag(String name){
		this.name = name;
	}
	
	public Tag(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public int getUserId(){
		return user_id;
	}
	
	public String getName(){
		return name;
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
	
	public int getNumPosts(){
		return num_posts;
	}
}
