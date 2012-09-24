package com.renren.dp.xlog.client.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import com.renren.dp.xlog.client.util.LogFileUtil;

import xlog.slice.LogData;

public class LogWriter {

	private String cacheLogDir=null;
	private FileWriter fw=null;
	private String fileName=null;
	private BufferedWriter br=null;
	
	private final ReentrantLock lock = new ReentrantLock();
	
	public LogWriter(String cacheLogDir) throws IOException{
		this.cacheLogDir=cacheLogDir;
		
		fileName=LogFileUtil.getLogFileName();
		File logFile=new File(cacheLogDir+"/"+fileName);
		fw=new FileWriter(logFile);
		br=new BufferedWriter(fw);
	}
	
	public boolean write(LogData[] logDatas){
		if(logDatas==null){
			return true;
		}
		lock.lock();
		String fileNameTmp=LogFileUtil.getLogFileName();
		if(!fileName.equals(fileNameTmp)){
			fileName=fileNameTmp;
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			File logFile=new File(cacheLogDir+"/"+fileName);
			try {
				fw=new FileWriter(logFile);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			br=new BufferedWriter(fw);
		}
		String categories=null;
		for(LogData logData:logDatas){
			categories=transformCategories(logData.categories);
			if(categories==null || "".equals(categories)){
				continue;
			}
			for(String log:logData.logs){
				try {
					br.write(categories+LogFileUtil.LOG_FILE_FIELD_SEPARATOR+log+"\n");
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		try {
			br.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lock.unlock();
		return true;
	}
	
	public String transformCategories(String[] categories){
		if(categories==null){
			return null;
		}
		if(categories.length==1){
			return categories[0];
		}
		StringBuilder sb=new StringBuilder();
		int len=categories.length;
		for(int i=0;i<len;i++){
			if(i>0){
				sb.append("/");
			}
			sb.append(categories[i]);
		}
		String str=sb.toString();
		sb.delete(0,sb.length());
		sb=null;
		return str;
	}
}
