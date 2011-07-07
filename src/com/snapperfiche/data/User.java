package com.snapperfiche.data;

public class User {
	private int id;
	private String email;
	private String alias;
	private String accounttype;
	private String first_name;
	private String last_name;
	
	public User(){}
	
	public int getId(){
		return id;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getAlias(){
		return alias;
	}
	
	public String getAccountType(){
		return accounttype;
	}
	
	public class Friend extends User {
		private String friend_time;
		public Friend(){}
		
		public String getFriendTime(){
			return friend_time;
		}
	}
	
	public String getFirstName(){
		return first_name;
	}
	
	public String getLastName(){
		return last_name;
	}
}
