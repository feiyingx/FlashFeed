package com.snapperfiche.data;

public class Tag {
	private int id;
	private int user_id;
	private String name;
	
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
}
