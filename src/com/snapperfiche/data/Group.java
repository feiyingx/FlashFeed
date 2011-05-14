package com.snapperfiche.data;

import com.snapperfiche.code.Enumerations;

public class Group {
	private int id;
	private int user_id;
	private String name;
	private String group_type;
	
	public Group(){}
	
	public Group(int id, String name){
		this.id = id;
		this.name = name;
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
	
	public Enumerations.GroupType getGroupType(){
		return Enumerations.GroupType.getGroupType(group_type);
	}
}
