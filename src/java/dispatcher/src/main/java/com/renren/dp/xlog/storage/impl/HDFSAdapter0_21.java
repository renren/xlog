package com.renren.dp.xlog.storage.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.renren.dp.xlog.cache.CacheFileMeta;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.storage.StorageAdapter;
import com.renren.dp.xlog.util.Constants;
import com.renren.dp.xlog.util.LogDataFormat;

public class HDFSAdapter0_21 extends HDFSAdapter{

	private static Logger logger = LoggerFactory.getLogger(HDFSAdapter0_21.class);
	
	public HDFSAdapter0_21(String uuid,int bufferSize){
		super(uuid,bufferSize);
	}
	
	public boolean flush(FSDataOutputStream hdfsOutput,String path){
		/**compile 1.0.3 */
//		try {
//			hdfsOutput.hflush();
//		} catch (IOException e) {
//			logger.error("fail to flush data to hdfs,and get hdfs ouputstream again! the exception is : "+e.getMessage());
//			try {
//				hdfsOutput.close();
//			} catch (IOException e1) {
//				logger.error("fail to close hdfs outputstream",e1.getMessage());
//			}
//			hdfsOutput=getHDFSOutputStream(path);
//			try {
//				if(hdfsOutput !=null){
//					hdfsOutput.hflush();
//					logger.info("success to reflush data to hdfs! ");
//				}
//			} catch (IOException e1) {
//				logger.error("fail to reflush data to hdfs! the exception is : "+e.getMessage());
//				return false;
//			}
//		}
		return true;
	}

	@Override
	public void initialize() throws IOException {
		Configuration conf=new Configuration();
		conf.set("dfs.replication",dfsReplication);
		conf.set("dfs.socket.timeout", socketTimeOut.toString());
		if(fs!=null){
			fs.close();
		}
		/**compile 1.0.3 */
		//fs=FileSystem.newInstance(URI.create(hdfsURI),conf);
	}
}
