package com.renren.dp.xlog.client.util;

import java.io.File;
import java.io.FileFilter;

public class LogFileFilter implements FileFilter{

	private String fileName=null;
	
	public void setFilterFileName(String fileName){
		this.fileName=fileName;
	}

	@Override
	public boolean accept(File pathname) {
		if(fileName==null){
			return true;
		}
		if(fileName.equals(pathname.getName())){
			return false;
		}
		return true;
	}
	
	
}
