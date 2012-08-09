package com.renren.dp.xlog.client.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFileUtil {

	public static final String LOG_FILE_FIELD_SEPARATOR=",";
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH");
	
	public static synchronized String getLogFileName(){
		return sdf.format(new Date());
	}
}
