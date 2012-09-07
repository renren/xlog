package com.renren.dp.xlog.storage.impl;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFSAdapter1_0_3 extends HDFSAdapter{

	private static Logger logger = LoggerFactory.getLogger(HDFSAdapter1_0_3.class);
	
	public HDFSAdapter1_0_3(String uuid,int bufferSize){
		super(uuid,bufferSize);
	}
	
	public boolean flush(FSDataOutputStream hdfsOutput,String path){
		try {
			hdfsOutput.flush();
		} catch (IOException e) {
			logger.error("fail to flush data to hdfs,and get hdfs ouputstream again! the exception is : "+e.getMessage());
			try {
				hdfsOutput.close();
			} catch (IOException e1) {
				logger.error("fail to close hdfs outputstream",e1.getMessage());
			}
			hdfsOutput=getHDFSOutputStream(path);
			try {
				if(hdfsOutput !=null){
					hdfsOutput.flush();
					logger.info("success to reflush data to hdfs! ");
				}
			} catch (IOException e1) {
				logger.error("fail to reflush data to hdfs! the exception is : "+e.getMessage());
				return false;
			}
		}
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
		fs=FileSystem.get(URI.create(hdfsURI),conf);
	}
}
