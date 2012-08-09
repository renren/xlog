package com.renren.dp.xlog.client;

public class XlogClientFactory {

	public static XlogClient asyncClient=null;
	public static XlogClient syncClient=null;
	
	public static synchronized XlogClient getInstance(boolean isAsyncClient){
		if(isAsyncClient){
			if(asyncClient==null){
				asyncClient=new AsyncClient();
			}
			return asyncClient;
		}else{
			if(syncClient==null){
				syncClient=new SyncClient();
			}
			return syncClient;
		}
	}
}
