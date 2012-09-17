package com.renren.dp.xlog.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractFileNameHandler {

	protected SimpleDateFormat sdf =null;
	
	public abstract SimpleDateFormat getFileNameDataFormat();
	
	public abstract String getCacheLogFileNum();
	
	public abstract int getFileNameDataFormatLen();
	
	public String getHDFSLogFileNum(){
		return sdf.format(new Date()).substring(0,13);
	}
	
	public String getHDFSLogFileNum(String strDate){
		return strDate.substring(0,13);
	}
}
