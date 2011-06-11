package com.snapperfiche.webservices;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class BaseService {
	private static DefaultHttpClient httpClient;
	public static synchronized DefaultHttpClient GetHttpClient(){
		if(httpClient == null){
			HttpParams params = new BasicHttpParams();
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http",
					PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https",
					SSLSocketFactory.getSocketFactory(), 443));
			
			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, registry);
			
			httpClient = new DefaultHttpClient(cm, params);
		}
		return httpClient;
	}
}
