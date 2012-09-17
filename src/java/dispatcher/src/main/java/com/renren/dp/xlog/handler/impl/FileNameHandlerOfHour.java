package com.renren.dp.xlog.handler.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.util.Constants;

public class FileNameHandlerOfHour extends AbstractFileNameHandler{

	public FileNameHandlerOfHour(){
		sdf = new SimpleDateFormat(Constants.FILE_NAME_FORMAT_HOUR);
	}
	
	@Override
	public String getCacheLogFileNum() {
		return sdf.format(new Date());
	}

	@Override
	public SimpleDateFormat getFileNameDataFormat() {
		// TODO Auto-generated method stub
		return sdf;
	}

	@Override
	public int getFileNameDataFormatLen() {
		return Constants.FILE_NAME_FORMAT_HOUR.length();
	}
}
