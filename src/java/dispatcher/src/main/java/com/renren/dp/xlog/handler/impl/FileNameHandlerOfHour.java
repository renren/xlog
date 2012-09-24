package com.renren.dp.xlog.handler.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.util.Constants;

public class FileNameHandlerOfHour extends AbstractFileNameHandler {

	public FileNameHandlerOfHour() {
		threadLocal = new ThreadLocal<SimpleDateFormat>() {
			protected synchronized SimpleDateFormat initialValue() {
				return new SimpleDateFormat(Constants.FILE_NAME_FORMAT_HOUR);
			}
		};
	}

	@Override
	public String getCacheLogFileNum() {
		return getDateFormat().format(new Date());
	}

	@Override
	public int getFileNameDataFormatLen() {
		return Constants.FILE_NAME_FORMAT_HOUR.length();
	}
}
