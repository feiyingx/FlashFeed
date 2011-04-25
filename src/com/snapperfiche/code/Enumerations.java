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
	}
}
