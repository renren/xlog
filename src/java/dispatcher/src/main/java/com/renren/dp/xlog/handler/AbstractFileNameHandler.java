package com.renren.dp.xlog.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractFileNameHandler {

	protected ThreadLocal<SimpleDateFormat> threadLocal =null;
	
	protected SimpleDateFormat getDateFormat() {
		return threadLocal.get();
	}
	
	public abstract String getCacheLogFileNum();
	
	public abstract int getFileNameDataFormatLen();
	
	public String getHDFSLogFileNum(){
		return  getDateFormat().format(new Date()).substring(0,13);
	}

	public SimpleDateFormat getFileNameDataFormat() {
		return getDateFormat();
	}
	
	public String getHDFSLogFileNum(String strDate){
		return strDate.substring(0,13);
	}
}
