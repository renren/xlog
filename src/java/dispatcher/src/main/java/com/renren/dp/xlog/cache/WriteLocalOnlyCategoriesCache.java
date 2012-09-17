package com.renren.dp.xlog.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.util.Constants;
import com.renren.dp.xlog.util.LogDataFormat;

import xlog.slice.LogData;

public class WriteLocalOnlyCategoriesCache {

	private Set<String> categoriesSet=null;
	
	private final static Logger logger = LoggerFactory.getLogger(WriteLocalOnlyCategoriesCache.class);
	
	public void initialize() throws IOException{
		categoriesSet=new HashSet<String>();
		File categoriesFile=new File(WriteLocalOnlyCategoriesCache.class.getResource("/conf/"+Constants.WRITE_LOCAL_ONLY_CATEGORIES_LIST).getFile());
		loadCategoriesFile(categoriesFile);
		
		Thread t=new FileMonitor(categoriesFile,categoriesFile.lastModified());
		t.setDaemon(true);
		t.start();
	}
	
	public boolean isWriteLocalOnly(LogData logData){
		synchronized(categoriesSet){
			String categories=LogDataFormat.transformCategories(logData.categories);
			if(categoriesSet.contains(categories)){
				return true;
			}
			return false;
		}
	}
	
	public Set<String> getCategories(){
		synchronized(categoriesSet){
			return this.categoriesSet;
		}
	}
	
	private void loadCategoriesFile(File f) throws IOException{
		synchronized(categoriesSet){
			Set<String> set=new HashSet<String>();
			FileReader fr=new FileReader(f);
			BufferedReader br=new BufferedReader(fr);
			String categories=null;
			while((categories=br.readLine())!=null){
				set.add(categories);
			}
			try{
				if(fr!=null){
					fr.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(br!=null){
					br.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			categoriesSet.clear();
			categoriesSet.addAll(set);
			set.clear();
			set=null;
			if(logger.isDebugEnabled()){
				logger.debug("WRITE LOCAL ONLY CATEGORIES LIST such as :");
				for(String s:categoriesSet){
					logger.debug(s);
				}
			}
		}
	}
	
	private class FileMonitor extends Thread{
		private File categoriesFile=null;
		private long lastModified;
		private long interval;
		
		FileMonitor(File categoriesFile,long lastModified){
			this.categoriesFile=categoriesFile;
			this.lastModified=lastModified;
			this.interval=1000*Configuration.getLong("categories.list.file.check.interval",Constants.WRITE_LOCAL_ONLY_CATEGORIES_LIST_FILE_CHECK_INTERVAL);
		}
		public void run(){
			while(true){
				if(categoriesFile.lastModified()!=lastModified){
					try {
						logger.info("WRITE_LOCAL_ONLY_CATEGORIES_LIST File has changed!");
						loadCategoriesFile(categoriesFile);
					} catch (IOException e) {
						logger.error("Fail to load Categories File!",e);
						continue;
					}
					lastModified=categoriesFile.lastModified();
				}
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
