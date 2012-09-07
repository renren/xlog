package com.renren.dp.xlog.storage;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.storage.impl.HDFSAdapter0_21;
import com.renren.dp.xlog.storage.impl.HDFSAdapter1_0_3;

public class StorageAdapterFactory {

	public static StorageAdapter getInstance(){
		StorageAdapter storageAdapter=null;
		String storageType=Configuration.getString("storage.type","");
		String storageVersion=Configuration.getString("storage.version","");
		int bufferSize=Configuration.getInt("hdfs.buffer.size", 4000);
		if(storageType.equals("hdfs")){
			if(storageVersion.equals("1.0.3")){
				storageAdapter=new HDFSAdapter1_0_3(Configuration.getString("xlog.uuid"),bufferSize);
			}else if(storageVersion.equals("0.21")){
				storageAdapter=new HDFSAdapter0_21(Configuration.getString("xlog.uuid"),bufferSize);
			}
		}
		return storageAdapter;
	}
}
