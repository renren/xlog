package com.renren.dp.xlog.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.renren.dp.xlog.client.util.LogFileFilter;
import com.renren.dp.xlog.client.util.LogFileUtil;

import xlog.slice.LogData;

public class LogSyncHandler extends Thread{

	private File  cacheLogDir =null;
	private AsyncClient client=null;
	private static final float threshold=0.5f;
	
	public LogSyncHandler(String strCacheLogDir,AsyncClient client){
		cacheLogDir=new File(strCacheLogDir);
		this.client=client;
	}
	
	public void run(){
		FileReader fr=null;
		BufferedReader br=null;
		String line=null;
		LogData logData=null;
		LogFileFilter filter =new LogFileFilter();
		while(true){
			filter.setFilterFileName(LogFileUtil.getLogFileName());
			File[] files=cacheLogDir.listFiles(filter);
			if(files==null || files.length==0){
				//sleep 15min
				try {
					Thread.sleep(900000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				for(File f:files){
					try {
						fr=new FileReader(f);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					br=new BufferedReader(fr);
					try {
						while((line=br.readLine())!=null){
							logData=transformToLogData(line);
							while(client.getQueueUseRate()>threshold){
								try {
									Thread.sleep(60000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							client.doSend(new LogData[]{logData});
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fr=null;
					br=null;
					f.delete();
				}
				
			}
		}
	}
	
	private LogData transformToLogData(String line){
		LogData logData = new LogData();
		int index=line.indexOf(LogFileUtil.LOG_FILE_FIELD_SEPARATOR);
		logData.categories=new String[]{line.substring(0, index)};
		logData.logs=new String[]{line.substring(index+1)};
		
		return logData;
	}
}
