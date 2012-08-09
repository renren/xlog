package com.renren.dp.xlog.cache;

import com.renren.dp.xlog.cache.impl.AsyncCacheManager;

public class CacheManagerFactory {

	private static CacheManager cm=null;
	
	public static synchronized CacheManager getInstance(){
		if(cm==null){
			cm=new AsyncCacheManager();
		}
		
		return cm;
	}
}
