package com.renren.dp.xlog.sync;

import java.io.File;
import java.io.FileFilter;
import java.util.TimerTask;

import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.io.CacheLogFileFilter;

public class ClearTimer extends TimerTask{

	private int localStorageCacheTime;
	private int errorDataCacheTime;
	private File errorDataLogDir;
	private File cacheLogDir;
	public ClearTimer(int localStorageCacheTime,int errorDataCacheTime){
		this.localStorageCacheTime=localStorageCacheTime;
		this.errorDataCacheTime=errorDataCacheTime;
		String storePath=Configuration.getString("oplog.store.path");
		errorDataLogDir=new File(storePath+"/"+Configuration.getString("storage.type"));
		cacheLogDir=new File(storePath+"/"+CacheManager.CACHE_TYPE);
	}
	@Override
	public void run() {
		if(localStorageCacheTime>0){
			CacheLogFileFilter opLogCLFF=new CacheLogFileFilter(localStorageCacheTime);
			if(cacheLogDir.exists()){
				clearUp(cacheLogDir,opLogCLFF);
			}
		}
		
		if(errorDataCacheTime>0){
			CacheLogFileFilter storageCLFF=new CacheLogFileFilter(errorDataCacheTime);
			if(errorDataLogDir.exists()){
				clearUp(errorDataLogDir,storageCLFF);
			}
		}
	}

	private void clearUp(File logFile,FileFilter fileFilter){
		if(logFile.isFile()){
			logFile.delete();
		}else{
			File[] subFiles=logFile.listFiles(fileFilter);
			for(File f:subFiles){
				clearUp(f,fileFilter);
			}
		}
	}
}
