package com.snapperfiche.code;

public class Enumerations {
	public enum LoginStatus{
		SUCCESS(1),
		FAILED(0),
		ERROR(-1);
		
		final int value;
		LoginStatus(int val){
			this.value = val;
		}
		
		public int value(){
			return value;
		}
	}
	
	public enum RegisterStatus{
		SUCCESS(1),
		FAILED_EXISTS(0),
		ERROR(-1);
		
		final int value;
		RegisterStatus(int val){
			this.value = val;
		}
		
		public int value(){
			return value;
		}
	}
	
	public enum BasicStatus{
		SUCCESS(1),
		NO_RESULTS(0),
		ERROR(-1),
		ERROR_NOT_AUTHENTICATED(-2);
		
		final int value;
		BasicStatus(int val){
			this.value = val;
		}
		
		public int value(){
			return value;
		}
		
		public static BasicStatus getStatus(int val){
			switch(val){
				case 1:
					return SUCCESS;
				case 0:
					return NO_RESULTS;
				case -2:
					return ERROR_NOT_AUTHENTICATED;
				default:
					return ERROR;
			}
		}
	}
	
	public enum PostType{
		NONE(""),
		DEFAULT("default"),
		QUESTION("question"),
		ANSWER("answer");
		
		final String value;
		PostType(String val){
			this.value = val;
		}
		
		public String value(){
			return value;
		}
		
		public static PostType getPostType(String val){
			if(val == "default")
				return DEFAULT;
			if(val ==  "question")
				return QUESTION;
			if(val == "answer")
				return ANSWER;
			
			return NONE;
		}
	}
	
	public enum GroupType{
		NONE(""),
		USER("user"),
		TAG("tag"),
		USER_FEED("user_feed"),
		TAG_FEED("tag_feed");
		
		final String value;
		GroupType(String val){
			this.value = val;
		}
		
		public String value(){
			return value;
		}
		
		public static GroupType getGroupType(String val){
			if(val == "user")
				return USER;
			if(val ==  "tag")
				return TAG;
			if(val == "user_feed")
				return USER_FEED;
			if(val == "tag_feed")
				return TAG_FEED;
			
			return NONE;
		}
	}
	
	public enum NotificationType{
		NEW_POST(1),
		NEW_QUESTION(2),
		NEW_ANSWER(3);
		
		final int value;
		NotificationType(int val){
			this.value = val;
		}
		
		public int value(){
			return value;
		}
		
		public static NotificationType getType(int val){
			switch(val){
				case 1:
					return NEW_POST;
				case 2:
					return NEW_QUESTION;
				case 3:
					return NEW_ANSWER;
				default:
					return NEW_POST;
			}
		}
	}
	
	public enum AudienceType{
		ALL(0),
		FRIENDS(1),
		SELECT(2);
		
		final int value;
		AudienceType(int val){
			this.value = val;
		}
		
		public int value(){
			return value;
		}
		
		public static AudienceType getType(int val){
			switch(val){
				case 0:
					return ALL;
				case 1:
					return FRIENDS;
				default:
					return SELECT;
			}
		}
	}
	
	public enum AccountType{
		DEFAULT(0),
		FACEBOOK(1);
		
		final int value;
		AccountType(int val){
			this.value = val;
		}
		
		public int value(){
			return value;
		}
		
		public static AccountType getType(int val){
			switch(val){
				case 0:
					return DEFAULT;
				case 1:
					return FACEBOOK;
				default:
					return DEFAULT;
			}
		}
	}
}
