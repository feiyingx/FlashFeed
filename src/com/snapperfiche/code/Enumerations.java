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
}
