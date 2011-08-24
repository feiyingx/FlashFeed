package com.snapperfiche.data;

import com.snapperfiche.code.Utility;
import com.snapperfiche.webservices.AccountService;

public class User {
	private int id;
	private String email;
	private String alias;
	private String accounttype;
	private String first_name;
	private String last_name;
	private String photo_file_name;
	
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
	
	public String getPhotoUrl(){
		if(Utility.IsNullOrEmpty(photo_file_name)) return "";
		
		String url = "/images/upload/" + AccountService.getUser().getId() + "/" + photo_file_name.replace(".jpg", "_original.jpg");
		return Utility.GetAbsoluteUrl(url);
	}
}
