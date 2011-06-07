package com.snapperfiche.webservices;

import org.apache.http.impl.client.DefaultHttpClient;

public class BaseService {
	private static DefaultHttpClient httpClient;
	public static synchronized DefaultHttpClient GetHttpClient(){
		if(httpClient == null){
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}
}
