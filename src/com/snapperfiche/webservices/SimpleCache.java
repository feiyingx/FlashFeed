package com.snapperfiche.webservices;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.github.droidfu.cachefu.AbstractCache;
import com.google.common.collect.MapMaker;
import com.snapperfiche.code.AppConfig;

public class SimpleCache{
	private static ConcurrentMap<String, Object> cache;
	private static final long _defaultExpirationMin = 1;
	private static int _defaultInitialCapacity = 100;
	private static int _maxConcurrentThreads = 5;
	
	public static void put(String cacheKey, Object data){
		if(cache == null){
			initCache();
		}
		cache.put(cacheKey, data);
	}
	
	public static Object get(String cacheKey){
		if(cache == null){
			initCache();
		}
		
		if(cache.containsKey(cacheKey)){
			return cache.get(cacheKey);
		}
		
		return null;
	}
	
	public static void remove(String cacheKey){
		if(cache == null){
			initCache();
		}
		
		if(cache.containsKey(cacheKey)){
			cache.remove(cacheKey);
		}else{
			Log.e(AppConfig.LogKey_CacheError, "Tried to remove item from cache with invalid cache key ".concat(cacheKey));
		}
	}
		
	private static void initCache(){
		MapMaker mapMaker = new MapMaker();
		mapMaker.initialCapacity(_defaultInitialCapacity);
		mapMaker.expiration(_defaultExpirationMin*60, TimeUnit.SECONDS);
		mapMaker.concurrencyLevel(_maxConcurrentThreads);
		mapMaker.softValues();
		cache = mapMaker.makeMap();
	}
}
